package com.ltcoe.meshvault.util

import android.content.Context
import android.os.Environment
import java.io.File
import java.io.FileOutputStream
import javax.crypto.SecretKey

object DownloadManager {

    suspend fun downloadAndDecryptFile(
        context: Context,
        fileId: String,
        fileName: String,
        fileKey: SecretKey?
    ): Boolean {
        return try {
            // 1. Create a temporary file in the phone's cache
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val outputFile = File(downloadsDir, fileName)
            val fos = FileOutputStream(outputFile)

            var chunkIndex = 0
            var hasMoreChunks = true

            while (hasMoreChunks) {
                // 2. Fetch the encrypted chunk from Arch Linux
                val encryptedBytes = ApiClient.downloadChunk(fileId, chunkIndex)

                if (encryptedBytes != null) {
                    // 3. Decrypt using the math we added earlier
                    val decryptedBytes = CryptoManager.decryptChunk(encryptedBytes, fileKey)

                    // 4. Write to the final file
                    fos.write(decryptedBytes)
                    chunkIndex++
                } else {
                    // No more chunks found on server
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