package com.ltcoe.meshvault.util

import android.content.Context
import java.io.File
import java.io.FileOutputStream
import javax.crypto.SecretKey

object DownloadManager {

    suspend fun downloadAndDecryptFile(
        context: Context,
        fileId: String,
        fileName: String,
        fileKey: SecretKey
    ): Boolean {
        return try {
            // FIX: Use the app's internal cache directory instead of public Downloads
            // This avoids all Android Scoped Storage EPERM crashes!
            val safeFileName = fileName.replace(":", "_")
            val outputFile = File(context.cacheDir, safeFileName)

            val fos = FileOutputStream(outputFile)

            var chunkIndex = 0
            var hasMoreChunks = true

            while (hasMoreChunks) {
                // Fetch encrypted chunk from Arch Linux
                val encryptedBytes = ApiClient.downloadChunk(fileId, chunkIndex)

                if (encryptedBytes != null) {
                    // Decrypt the chunk
                    val decryptedBytes = CryptoManager.decryptChunk(encryptedBytes, fileKey)

                    // Write to the file
                    fos.write(decryptedBytes)
                    chunkIndex++
                } else {
                    hasMoreChunks = false
                }
            }

            fos.close()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}