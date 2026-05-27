package com.bsit.peerx.network

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import com.bsit.peerx.network.model.NetworkPacket
import com.bsit.peerx.network.model.PacketType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.*
import java.io.*
import java.net.ServerSocket

class FileTransferServer(
    private val context: Context,
    private val port: Int = 8989
) {
    private val json = Json { ignoreUnknownKeys = true }
    private var serverSocket: ServerSocket? = null

    suspend fun receiveFile(
        fileTransferId: String,
        fileName: String,
        mimeType: String,
        totalSize: Long,
        onProgress: (Float) -> Unit
    ): Result<Uri> = withContext(Dispatchers.IO) {
        try {
            serverSocket = ServerSocket(port)
            Log.d(TAG, "FileTransferServer started on port $port, waiting for connection...")
            
            val socket = serverSocket!!.accept()
            Log.d(TAG, "Connection accepted from ${socket.inetAddress?.hostAddress}")
            
            val dis = DataInputStream(BufferedInputStream(socket.getInputStream()))
            val tempFile = File(context.cacheDir, "temp_${fileTransferId}_$fileName")
            val fos = FileOutputStream(tempFile)
            
            var totalBytesReceived = 0L
            
            try {
                var isComplete = false
                while (!isComplete) {
                    val length = dis.readInt()
                    if (length <= 0) break
                    val buffer = ByteArray(length)
                    dis.readFully(buffer)
                    
                    val packet = json.decodeFromString<NetworkPacket>(String(buffer))
                    when (packet.type) {
                        PacketType.FILE_CHUNK -> {
                            val chunkId = packet.payload.jsonObject["fileTransferId"]?.jsonPrimitive?.content
                            if (chunkId == fileTransferId) {
                                val dataBase64 = packet.payload.jsonObject["data"]?.jsonPrimitive?.content ?: ""
                                val data = Base64.decode(dataBase64, Base64.DEFAULT)
                                fos.write(data)
                                totalBytesReceived += data.size
                                onProgress(totalBytesReceived.toFloat() / totalSize)
                            }
                        }
                        PacketType.FILE_COMPLETE -> {
                            val completeId = packet.payload.jsonObject["fileTransferId"]?.jsonPrimitive?.content
                            if (completeId == fileTransferId) {
                                isComplete = true
                            }
                        }
                        else -> {
                            Log.w(TAG, "Received unexpected packet type: ${packet.type}")
                        }
                    }
                }
                fos.flush()
            } finally {
                fos.close()
                socket.close()
                serverSocket?.close()
                serverSocket = null
            }
            
            val uri = saveToMediaStore(tempFile, fileName, mimeType)
            tempFile.delete()
            
            if (uri != null) {
                Log.d(TAG, "File saved to MediaStore: $uri")
                Result.success(uri)
            } else {
                Result.failure(IOException("Failed to save to MediaStore"))
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "File receive failed: ${e.message}")
            Result.failure(e)
        }
    }

    private fun saveToMediaStore(file: File, fileName: String, mimeType: String): Uri? {
        val values = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
        }
        val uri = context.contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
        uri?.let {
            context.contentResolver.openOutputStream(it)?.use { outputStream ->
                file.inputStream().use { inputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
        }
        return uri
    }

    companion object {
        private const val TAG = "FileTransferServer"
    }
}



