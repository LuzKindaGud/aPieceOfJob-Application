package com.bsit.peerx.socket

import android.net.wifi.p2p.WifiP2pInfo
import android.util.Base64
import android.util.Log
import com.bsit.peerx.crypto.KeyStoreManager
import com.bsit.peerx.data.local.dao.PeerDao
import com.bsit.peerx.data.local.entity.PeerEntity
import com.bsit.peerx.data.repository.ProfileRepository
import com.bsit.peerx.network.model.NetworkPacket
import com.bsit.peerx.network.model.PacketType
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.*
import java.io.*
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.net.Socket
import java.security.SecureRandom

class SocketMessageTransport(
    private val externalScope: CoroutineScope,
    private val keyStoreManager: KeyStoreManager,
    private val peerDao: PeerDao,
    private val profileRepository: ProfileRepository
) : MessageTransport {

    private val json = Json { ignoreUnknownKeys = true }
    private val _incomingPackets = MutableSharedFlow<NetworkPacket>(extraBufferCapacity = 64)
    override fun incomingPackets(): Flow<NetworkPacket> = _incomingPackets.asSharedFlow()

    @Deprecated("Use incomingPackets instead", ReplaceWith("incomingPackets()"))
    fun incomingMessages(): Flow<Pair<String, String>> = _incomingPackets.asSharedFlow().map { packet ->
        if (packet.type == PacketType.TEXT) {
            packet.senderId to (packet.payload.jsonObject["text"]?.jsonPrimitive?.content ?: "")
        } else {
            "" to ""
        }
    }.filter { it.first.isNotEmpty() }

    private val _connectionState = MutableStateFlow(ConnectionStatus.DISCONNECTED)
    val connectionState: StateFlow<ConnectionStatus> = _connectionState.asStateFlow()

    private val _connectedPeerAddress = MutableStateFlow<String?>(null)
    val connectedPeerAddress: StateFlow<String?> = _connectedPeerAddress.asStateFlow()

    private val _connectedPeerId = MutableStateFlow<String?>(null)
    val connectedPeerId: StateFlow<String?> = _connectedPeerId.asStateFlow()

    private var clientSocket: Socket? = null
    private var serverSocket: ServerSocket? = null
    private var communicationJob: Job? = null

    private var dos: DataOutputStream? = null
    private var dis: DataInputStream? = null

    companion object {
        private const val TAG = "SocketTransport"
        private const val PORT = 8988
    }

    fun startServer() {
        if (_connectionState.value != ConnectionStatus.DISCONNECTED) return
        _connectionState.value = ConnectionStatus.CONNECTING

        externalScope.launch(Dispatchers.IO) {
            try {
                serverSocket = ServerSocket(PORT)
                Log.d(TAG, "Server started, waiting for client...")
                val socket = serverSocket!!.accept()
                handleNewConnection(socket, isGroupOwner = true)
            } catch (e: IOException) {
                Log.e(TAG, "Server error: ${e.message}")
                disconnect()
            }
        }
    }

    fun connectToServer(ipAddress: String) {
        if (_connectionState.value != ConnectionStatus.DISCONNECTED) return
        _connectionState.value = ConnectionStatus.CONNECTING

        externalScope.launch(Dispatchers.IO) {
            try {
                val socket = Socket()
                socket.connect(InetSocketAddress(ipAddress, PORT), 5000)
                handleNewConnection(socket, isGroupOwner = false)
            } catch (e: IOException) {
                Log.e(TAG, "Client connection error: ${e.message}")
                disconnect()
            }
        }
    }

    private suspend fun handleNewConnection(socket: Socket, isGroupOwner: Boolean) {
        try {
            Log.d(TAG, "New connection established. IsGroupOwner: $isGroupOwner, Remote: ${socket.inetAddress?.hostAddress}")
            _connectionState.value = ConnectionStatus.CONNECTED
            
            clientSocket = socket
            dos = DataOutputStream(BufferedOutputStream(socket.getOutputStream()))
            dis = DataInputStream(BufferedInputStream(socket.getInputStream()))
            _connectedPeerAddress.value = socket.inetAddress?.hostAddress

            // Small delay to let the Wi-Fi Direct group stabilize
            delay(500)

            if (performHandshake(isGroupOwner)) {
                Log.d(TAG, "Handshake successful!")
                _connectionState.value = ConnectionStatus.CONNECTED_AND_VERIFIED
                startCommunication()
            } else {
                Log.e(TAG, "Handshake failed.")
                _connectionState.value = ConnectionStatus.AUTH_FAILED
                disconnect()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Connection handling failed: ${e.message}")
            e.printStackTrace()
            disconnect()
        }
    }

    private suspend fun performHandshake(isGroupOwner: Boolean): Boolean = withContext(Dispatchers.IO) {
        try {
            // Phase 1: Exchange Public Keys & Profile
            val myPublicKeyBase64 = Base64.encodeToString(keyStoreManager.getPublicKeyBytes(), Base64.NO_WRAP)
            val myDisplayName = profileRepository.getDisplayName().first()
            val myProfilePacket = NetworkPacket(
                type = PacketType.AUTH_PUBKEY_EXCHANGE,
                senderId = keyStoreManager.getPeerIdHash(),
                payload = buildJsonObject {
                    put("publicKey", myPublicKeyBase64)
                    put("displayName", myDisplayName)
                }
            )
            writePacket(myProfilePacket)

            val remoteProfile = readPacket()
            if (remoteProfile.type != PacketType.AUTH_PUBKEY_EXCHANGE) return@withContext false
            
            val remotePublicKeyBase64 = remoteProfile.payload.jsonObject["publicKey"]?.jsonPrimitive?.content ?: return@withContext false
            val remotePublicKeyBytes = Base64.decode(remotePublicKeyBase64, Base64.NO_WRAP)
            val remotePeerId = remoteProfile.senderId
            val remoteDisplayName = remoteProfile.payload.jsonObject["displayName"]?.jsonPrimitive?.content ?: "Unknown"

            // Phase 2 & 3: Mutual Authentication
            if (isGroupOwner) {
                // Server: Wait for client's challenge, then challenge the client back
                if (!handleChallenge(remotePublicKeyBytes)) return@withContext false
                if (!initiateChallenge(remotePublicKeyBytes)) return@withContext false
            } else {
                // Client: Challenge the server first, then wait for server's challenge
                if (!initiateChallenge(remotePublicKeyBytes)) return@withContext false
                if (!handleChallenge(remotePublicKeyBytes)) return@withContext false
            }

            // Handshake Success: Persist the verified peer
            _connectedPeerId.value = remotePeerId
            peerDao.insertPeer(
                PeerEntity(
                    peerId = remotePeerId,
                    publicKeyBase64 = remotePublicKeyBase64,
                    displayName = remoteDisplayName,
                    avatarHash = null,
                    macAddress = _connectedPeerAddress.value ?: "",
                    firstSeenAt = System.currentTimeMillis(),
                    lastSeenAt = System.currentTimeMillis()
                )
            )
            Log.d(TAG, "Handshake successful with peer: $remotePeerId")
            return@withContext true
        } catch (e: Exception) {
            Log.e(TAG, "Handshake failed: ${e.message}")
            false
        }
    }

    private fun initiateChallenge(remotePublicKey: ByteArray): Boolean {
        // 1. Send Challenge (nonce)
        val nonce = ByteArray(32).apply { SecureRandom().nextBytes(this) }
        val challengePacket = NetworkPacket(
            type = PacketType.AUTH_CHALLENGE,
            senderId = keyStoreManager.getPeerIdHash(),
            payload = buildJsonObject { put("nonce", Base64.encodeToString(nonce, Base64.NO_WRAP)) }
        )
        writePacket(challengePacket)

        // 2. Receive Response (signature)
        val response = try { readPacket() } catch (e: Exception) { return false }
        if (response.type != PacketType.AUTH_RESPONSE) return false
        val signatureBase64 = response.payload.jsonObject["signature"]?.jsonPrimitive?.content ?: return false
        val signature = Base64.decode(signatureBase64, Base64.NO_WRAP)

        // 3. Verify Signature
        return keyStoreManager.verify(remotePublicKey, nonce, signature)
    }

    private fun handleChallenge(remotePublicKey: ByteArray): Boolean {
        // 1. Receive Challenge (nonce)
        val challenge = try { readPacket() } catch (e: Exception) { return false }
        if (challenge.type != PacketType.AUTH_CHALLENGE) return false
        val nonceBase64 = challenge.payload.jsonObject["nonce"]?.jsonPrimitive?.content ?: return false
        val nonce = Base64.decode(nonceBase64, Base64.NO_WRAP)

        // 2. Sign Challenge
        val signature = keyStoreManager.sign(nonce)
        val responsePacket = NetworkPacket(
            type = PacketType.AUTH_RESPONSE,
            senderId = keyStoreManager.getPeerIdHash(),
            payload = buildJsonObject { put("signature", Base64.encodeToString(signature, Base64.NO_WRAP)) }
        )
        writePacket(responsePacket)
        return true
    }

    private fun writePacket(packet: NetworkPacket) {
        val bytes = json.encodeToString(packet).toByteArray()
        dos?.writeInt(bytes.size)
        dos?.write(bytes)
        dos?.flush()
    }

    private fun readPacket(): NetworkPacket {
        val length = dis?.readInt() ?: throw IOException("Socket closed")
        if (length <= 0 || length > 10 * 1024 * 1024) throw IOException("Invalid packet length: $length")
        val buffer = ByteArray(length)
        dis?.readFully(buffer)
        return json.decodeFromString(String(buffer))
    }

    private fun startCommunication() {
        communicationJob = externalScope.launch(Dispatchers.IO) {
            try {
                while (isActive) {
                    val packet = readPacket()
                    _incomingPackets.emit(packet)

                    // Auto-ACK for text messages
                    if (packet.type == PacketType.TEXT) {
                        val ackPacket = NetworkPacket(
                            type = PacketType.ACK_RECEIPT,
                            senderId = keyStoreManager.getPeerIdHash(),
                            payload = buildJsonObject {
                                put("originalPacketId", packet.packetId)
                            }
                        )
                        writePacket(ackPacket)
                    }
                }
            } catch (e: Exception) {
                Log.w(TAG, "Communication lost: ${e.message}")
            } finally {
                disconnect()
            }
        }
    }

    fun startCommunicationFrom(p2pInfo: WifiP2pInfo) {
        if (p2pInfo.isGroupOwner) {
            startServer()
        } else {
            p2pInfo.groupOwnerAddress?.hostAddress?.let { connectToServer(it) }
        }
    }

    override fun getRemoteAddress(): String? = _connectedPeerAddress.value

    override suspend fun sendPacket(packet: NetworkPacket): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            if (clientSocket?.isConnected == true && _connectionState.value == ConnectionStatus.CONNECTED_AND_VERIFIED) {
                writePacket(packet)
                Result.success(Unit)
            } else {
                Result.failure(IOException("Not connected and verified."))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Send packet failed: ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun send(peerAddress: String, text: String): Result<Unit> {
        val packet = NetworkPacket(
            type = PacketType.TEXT,
            senderId = keyStoreManager.getPeerIdHash(),
            payload = buildJsonObject { put("text", text) }
        )
        return sendPacket(packet)
    }

    fun disconnect() {
        _connectionState.value = ConnectionStatus.DISCONNECTED
        _connectedPeerAddress.value = null
        _connectedPeerId.value = null
        communicationJob?.cancel()
        try {
            dos = null
            dis = null
            clientSocket?.close()
            serverSocket?.close()
        } catch (e: IOException) {
            Log.e(TAG, "Error closing sockets: ${e.message}")
        }
        clientSocket = null
        serverSocket = null
        Log.d(TAG, "Disconnected.")
    }
}

enum class ConnectionStatus {
    DISCONNECTED,
    CONNECTING,
    CONNECTED,
    CONNECTED_AND_VERIFIED,
    AUTH_FAILED
}



