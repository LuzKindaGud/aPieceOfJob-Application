package com.bsit.peerx.data.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import com.bsit.peerx.crypto.KeyStoreManager
import com.bsit.peerx.data.datasource.ChatLocalDataSource
import com.bsit.peerx.data.local.dao.PeerDao
import com.bsit.peerx.data.local.entity.ChatMessageEntity
import com.bsit.peerx.data.model.ChatMessageModel
import com.bsit.peerx.data.model.MessageStatus
import com.bsit.peerx.data.model.MessageType
import com.bsit.peerx.data.model.SenderType
import com.bsit.peerx.network.FileTransferClient
import com.bsit.peerx.network.FileTransferServer
import com.bsit.peerx.network.model.CallSignalingEvent
import com.bsit.peerx.network.model.NetworkPacket
import com.bsit.peerx.network.model.PacketType
import com.bsit.peerx.socket.MessageTransport
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.*
import java.util.UUID

import com.bsit.peerx.network.CallManager

class ChatRepositoryImpl(
    private val context: Context,
    private val local: ChatLocalDataSource,
    private val peerDao: PeerDao,
    private val transport: MessageTransport,
    private val keyStoreManager: KeyStoreManager,
    private val scope: CoroutineScope,
    private val callManager: CallManager
) : ChatRepository {

    private val _typingStatuses = MutableSharedFlow<Pair<String, Boolean>>(extraBufferCapacity = 64)
    private val _fileProgressMap = MutableStateFlow<Map<String, Float>>(emptyMap())
    private val pendingFileTransfers = mutableMapOf<String, Uri>()

    // Call signaling events (SDP / ICE)
    private val _callSignaling = MutableSharedFlow<CallSignalingEvent>(extraBufferCapacity = 64)

    init {
        scope.launch {
            transport.incomingPackets().collect { packet ->
                when (packet.type) {
                    PacketType.TEXT -> {
                        val payload = packet.payload.jsonObject
                        val text = payload["text"]?.jsonPrimitive?.content ?: ""
                        val groupId = payload["groupId"]?.jsonPrimitive?.content
                        val storePeerAddress = groupId ?: packet.senderId

                        local.insert(
                            ChatMessageEntity(
                                packetId = packet.packetId,
                                text = text,
                                senderType = SenderType.RECEIVED.name,
                                messageType = MessageType.TEXT.name,
                                status = MessageStatus.NONE.name,
                                timestamp = packet.timestamp,
                                peerAddress = storePeerAddress,
                                senderId = packet.senderId
                            )
                        )
                    }
                    PacketType.ACK_RECEIPT -> {
                        val originalPacketId = packet.payload.jsonObject["originalPacketId"]?.jsonPrimitive?.content
                        val status = packet.payload.jsonObject["status"]?.jsonPrimitive?.content
                        if (originalPacketId != null) {
                            val newStatus = if (status == MessageStatus.READ.name) MessageStatus.READ.name else MessageStatus.DELIVERED.name
                            local.updateStatus(originalPacketId, newStatus)
                        }
                    }
                    PacketType.TYPING_STATUS -> {
                        val isTyping = packet.payload.jsonObject["isTyping"]?.jsonPrimitive?.boolean ?: false
                        _typingStatuses.emit(packet.senderId to isTyping)
                    }
                    PacketType.FILE_HEADER -> {
                        handleFileHeader(packet)
                    }
                    PacketType.FILE_READY -> {
                        val fileTransferId = packet.payload.jsonObject["fileTransferId"]?.jsonPrimitive?.content ?: return@collect
                        val uri = pendingFileTransfers.remove(fileTransferId) ?: return@collect
                        val remoteAddress = transport.getRemoteAddress() ?: return@collect
                        
                        scope.launch(Dispatchers.IO) {
                            val client = FileTransferClient(context, keyStoreManager.getPeerIdHash())
                            client.sendFile(remoteAddress, 8989, uri, fileTransferId) { progress ->
                                _fileProgressMap.update { it + (fileTransferId to progress) }
                            }
                        }
                    }
                    PacketType.CALL_SDP -> {
                        val payload = packet.payload.jsonObject
                        val sdp = payload["sdp"]?.jsonPrimitive?.content ?: ""
                        val callId = payload["callId"]?.jsonPrimitive?.content
                        // Forward signaling to CallManager
                        scope.launch {
                            try {
                                callManager.handleRemoteSdp(packet.senderId, callId, sdp)
                            } catch (e: Exception) {
                                Log.e("ChatRepository", "handleRemoteSdp failed: ${'$'}e")
                            }
                        }
                    }
                    PacketType.CALL_ICE_CANDIDATE -> {
                        val payload = packet.payload.jsonObject
                        val candidate = payload["candidate"]?.jsonPrimitive?.content ?: ""
                        val callId = payload["callId"]?.jsonPrimitive?.content
                        scope.launch {
                            try {
                                callManager.handleRemoteIceCandidate(packet.senderId, callId, candidate)
                            } catch (e: Exception) {
                                Log.e("ChatRepository", "handleRemoteIceCandidate failed: ${'$'}e")
                            }
                        }
                    }
                    else -> { /* Other packets ignored for now */ }
                }
            }
        }
    }

    override fun observeMessages(peerAddress: String): Flow<List<ChatMessageModel>> {
        return local.observeMessages(peerAddress).map { list ->
            list.map { entity ->
                ChatMessageModel(
                    id = entity.id,
                    packetId = entity.packetId,
                    text = entity.text,
                    senderType = SenderType.entries.firstOrNull { it.name == entity.senderType }
                        ?: SenderType.RECEIVED,
                    messageType = MessageType.valueOf(entity.messageType),
                    fileTransferId = entity.fileTransferId,
                    status = MessageStatus.valueOf(entity.status),
                    timestamp = entity.timestamp,
                    peerAddress = entity.peerAddress
                )
            }
        }
    }

    override suspend fun sendMessage(peerAddress: String, text: String): Result<Unit> {
        val packet = NetworkPacket(
            type = PacketType.TEXT,
            senderId = keyStoreManager.getPeerIdHash(),
            payload = buildJsonObject {
                put("text", text)
            }
        )

        // Save to local DB first as SENDING or SENT
        local.insert(
            ChatMessageEntity(
                packetId = packet.packetId,
                text = text,
                senderType = SenderType.SENT.name,
                messageType = MessageType.TEXT.name,
                status = MessageStatus.SENT.name,
                timestamp = packet.timestamp,
                peerAddress = peerAddress,
                senderId = keyStoreManager.getPeerIdHash()
            )
        )

        val sendResult = transport.sendPacket(packet)
        if (!sendResult.isSuccess) {
            local.updateStatus(packet.packetId, MessageStatus.FAILED.name)
        }

        return sendResult
    }

    override suspend fun sendGroupMessage(groupId: String, text: String): Result<Unit> {
        val packet = NetworkPacket(
            type = PacketType.TEXT,
            senderId = keyStoreManager.getPeerIdHash(),
            payload = buildJsonObject {
                put("text", text)
                put("groupId", groupId)
            }
        )

        local.insert(
            ChatMessageEntity(
                packetId = packet.packetId,
                text = text,
                senderType = SenderType.SENT.name,
                messageType = MessageType.TEXT.name,
                status = MessageStatus.SENT.name,
                timestamp = packet.timestamp,
                peerAddress = groupId,
                senderId = keyStoreManager.getPeerIdHash()
            )
        )

        val sendResult = transport.sendPacket(packet)
        if (!sendResult.isSuccess) {
            local.updateStatus(packet.packetId, MessageStatus.FAILED.name)
        }

        return sendResult
    }

    override fun observeTypingStatus(peerAddress: String): Flow<Boolean> {
        return _typingStatuses
            .filter { it.first == peerAddress }
            .map { it.second }
            .distinctUntilChanged()
    }

    override fun observeCallSignaling(): Flow<CallSignalingEvent> {
        return _callSignaling.asSharedFlow()
    }

    override suspend fun startCall(targetPeerId: String): Result<String> {
        return try {
            val callId = UUID.randomUUID().toString()
            // fire-and-forget: CallManager handles sending signaling packets
            callManager.startCall(targetPeerId, callId)
            Result.success(callId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun stopCall(callId: String): Result<Unit> {
        return try {
            callManager.stopCall(callId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun sendTypingStatus(peerAddress: String, isTyping: Boolean): Result<Unit> {
        val packet = NetworkPacket(
            type = PacketType.TYPING_STATUS,
            senderId = keyStoreManager.getPeerIdHash(),
            payload = buildJsonObject { put("isTyping", isTyping) }
        )
        return transport.sendPacket(packet)
    }

    override fun observeFileProgress(fileTransferId: String): Flow<Float> {
        return _fileProgressMap.map { it[fileTransferId] ?: 0f }
    }

    override suspend fun sendFile(peerAddress: String, uri: Uri): Result<Unit> {
        val fileTransferId = UUID.randomUUID().toString()
        val fileName = getFileName(uri)
        val mimeType = context.contentResolver.getType(uri) ?: "application/octet-stream"
        val totalSize = context.contentResolver.openAssetFileDescriptor(uri, "r")?.length ?: 0L
        
        pendingFileTransfers[fileTransferId] = uri
        
        val packet = NetworkPacket(
            type = PacketType.FILE_HEADER,
            senderId = keyStoreManager.getPeerIdHash(),
            payload = buildJsonObject {
                put("fileTransferId", fileTransferId)
                put("fileName", fileName)
                put("mimeType", mimeType)
                put("totalSize", totalSize)
            }
        )
        
        local.insert(
            ChatMessageEntity(
                packetId = packet.packetId,
                text = "File: $fileName",
                senderType = SenderType.SENT.name,
                messageType = MessageType.FILE.name,
                fileTransferId = fileTransferId,
                status = MessageStatus.SENDING.name,
                timestamp = packet.timestamp,
                peerAddress = peerAddress,
                senderId = keyStoreManager.getPeerIdHash()
            )
        )
        
        return transport.sendPacket(packet)
    }

    override suspend fun handleFileHeader(packet: NetworkPacket) {
        val payload = packet.payload.jsonObject
        val fileTransferId = payload["fileTransferId"]?.jsonPrimitive?.content ?: return
        val fileName = payload["fileName"]?.jsonPrimitive?.content ?: "unknown"
        val mimeType = payload["mimeType"]?.jsonPrimitive?.content ?: "application/octet-stream"
        val totalSize = payload["totalSize"]?.jsonPrimitive?.long ?: 0L
        
        // Save to local DB
        local.insert(
            ChatMessageEntity(
                packetId = packet.packetId,
                text = "Incoming file: $fileName",
                senderType = SenderType.RECEIVED.name,
                messageType = MessageType.FILE.name,
                fileTransferId = fileTransferId,
                status = MessageStatus.NONE.name,
                timestamp = packet.timestamp,
                peerAddress = packet.senderId,
                senderId = packet.senderId
            )
        )

        // Automatically accept and start server
        scope.launch(Dispatchers.IO) {
            // Signal readiness first (or after server starts?)
            // Usually we should start server first
            val server = FileTransferServer(context)
            
            // Send FILE_READY packet
            val readyPacket = NetworkPacket(
                type = PacketType.FILE_READY,
                senderId = keyStoreManager.getPeerIdHash(),
                payload = buildJsonObject {
                    put("fileTransferId", fileTransferId)
                }
            )
            transport.sendPacket(readyPacket)

            val result = server.receiveFile(fileTransferId, fileName, mimeType, totalSize) { progress ->
                _fileProgressMap.update { it + (fileTransferId to progress) }
            }
            
            if (result.isSuccess) {
                Log.d("ChatRepository", "File received: ${result.getOrNull()}")
            }
        }
    }

    private fun getFileName(uri: Uri): String {
        var name = "unknown"
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val index = it.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                if (index != -1) {
                    name = it.getString(index)
                }
            }
        }
        if (name == "unknown") {
            name = uri.path?.substringAfterLast('/') ?: "unknown"
        }
        return name
    }

    override suspend fun sendReadReceiptsForPeer(peerAddress: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val unread = local.getUnreadReceived(peerAddress)
            for (msg in unread) {
                val packetId = msg.packetId
                if (packetId != null) {
                    val packet = NetworkPacket(
                        type = PacketType.ACK_RECEIPT,
                        senderId = keyStoreManager.getPeerIdHash(),
                        payload = buildJsonObject {
                            put("originalPacketId", packetId)
                            put("status", MessageStatus.READ.name)
                        }
                    )
                    transport.sendPacket(packet)
                    local.updateStatus(packetId, MessageStatus.READ.name)
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getPeerName(peerId: String): String? {
        return peerDao.getPeerById(peerId)?.displayName
    }
}



