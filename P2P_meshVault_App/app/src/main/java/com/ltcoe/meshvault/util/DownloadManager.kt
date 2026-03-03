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
        fileKey: SecretKey?,
        isForPreview: Boolean
    ): File? {
        return try {
            val safeFileName = fileName.replace(":", "_")

            val targetDir = if (isForPreview) {
                context.cacheDir
            } else {
                context.getExternalFilesDir(android.os.Environment.DIRECTORY_DOWNLOADS) ?: context.cacheDir
            }

            val outputFile = File(targetDir, safeFileName)
            val fos = FileOutputStream(outputFile)

            var chunkIndex = 0
            var hasMoreChunks = true

            while (hasMoreChunks) {
                val encryptedBytes = ApiClient.downloadChunk(fileId, chunkIndex)
                if (encryptedBytes != null) {
                    val decryptedBytes = CryptoManager.decryptChunk(encryptedBytes, fileKey)
                    fos.write(decryptedBytes)
                    chunkIndex++
                } else {
                    hasMoreChunks = false
                }
            }
            fos.close()
            outputFile
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}