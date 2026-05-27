package com.bsit.peerx.network

import android.content.Context
import android.net.Uri
import android.util.Base64
import android.util.Log
import com.bsit.peerx.network.model.NetworkPacket
import com.bsit.peerx.network.model.PacketType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.*
import java.io.*
import java.net.InetSocketAddress
import java.net.Socket

class FileTransferClient(
    private val context: Context,
    private val senderId: String
) {
    private val json = Json { ignoreUnknownKeys = true }

    suspend fun sendFile(
        host: String,
        port: Int = 8989,
        uri: Uri,
        fileTransferId: String,
        onProgress: (Float) -> Unit
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val socket = Socket()
            socket.connect(InetSocketAddress(host, port), 5000)
            
            val dos = DataOutputStream(BufferedOutputStream(socket.getOutputStream()))
            val inputStream = context.contentResolver.openInputStream(uri) ?: throw IOException("Could not open URI")
            
            val totalSize = context.contentResolver.openAssetFileDescriptor(uri, "r")?.length ?: -1L
            val buffer = ByteArray(8 * 1024) // 8KB chunks
            var bytesRead: Int
            var totalBytesSent = 0L
            var chunkIndex = 0
            
            inputStream.use { input ->
                while (input.read(buffer).also { bytesRead = it } != -1) {
                    val chunkData = if (bytesRead == buffer.size) buffer else buffer.copyOf(bytesRead)
                    val packet = NetworkPacket(
                        type = PacketType.FILE_CHUNK,
                        senderId = senderId,
                        payload = buildJsonObject {
                            put("fileTransferId", fileTransferId)
                            put("chunkIndex", chunkIndex)
                            put("data", Base64.encodeToString(chunkData, Base64.DEFAULT))
                        }
                    )
                    
                    writePacket(dos, packet)
                    
                    totalBytesSent += bytesRead
                    chunkIndex++
                    if (totalSize > 0) {
                        onProgress(totalBytesSent.toFloat() / totalSize)
                    }
                }
            }
            
            val completePacket = NetworkPacket(
                type = PacketType.FILE_COMPLETE,
                senderId = senderId,
                payload = buildJsonObject {
                    put("fileTransferId", fileTransferId)
                }
            )
            writePacket(dos, completePacket)
            
            dos.flush()
            socket.close()
            Log.d(TAG, "File sent successfully: $fileTransferId")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "File send failed: ${e.message}")
            Result.failure(e)
        }
    }

    private fun writePacket(dos: DataOutputStream, packet: NetworkPacket) {
        val bytes = json.encodeToString(packet).toByteArray()
        dos.writeInt(bytes.size)
        dos.write(bytes)
    }

    companion object {
        private const val TAG = "FileTransferClient"
    }
}



