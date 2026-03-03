package com.ltcoe.meshvault.util

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID

data class FileChunk(
    val fileId: String,
    val chunkIndex: Int,
    val data: ByteArray
)

object FileSharder {
    private const val CHUNK_SIZE = 1024 * 1024 // 1MB

    /**
     * Now a 'suspend' function! It forces the caller to run it in a Coroutine.
     */
    suspend fun shardFile(context: Context, uri: Uri): List<FileChunk> {
        // Switch to the IO dispatcher for heavy disk reading
        return withContext(Dispatchers.IO) {
            val chunks = mutableListOf<FileChunk>()
            val fileId = UUID.randomUUID().toString()
            var index = 0

            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                val buffer = ByteArray(CHUNK_SIZE)
                var bytesRead = inputStream.read(buffer)

                while (bytesRead != -1) {
                    val exactData = if (bytesRead == CHUNK_SIZE) {
                        buffer.clone()
                    } else {
                        buffer.copyOf(bytesRead)
                    }

                    chunks.add(FileChunk(fileId, index, exactData))
                    index++
                    bytesRead = inputStream.read(buffer)
                }
            }

            // Return the chunks back to the Main Thread when done
            chunks
        }
    }
}