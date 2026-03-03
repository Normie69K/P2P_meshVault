package com.ltcoe.meshvault.util

import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.request.forms.*
import io.ktor.http.*

object ApiClient {
    private const val BASE_URL = "https://p2p-meshvault.onrender.com"

    private val client = HttpClient(Android)

    suspend fun uploadChunk(chunk: FileChunk): Boolean {
        return try {
            val response = client.submitFormWithBinaryData(
                url = "$BASE_URL/files/upload-chunk",
                formData = formData {
                    // Send the text data
                    append("fileId", chunk.fileId)
                    append("chunkIndex", chunk.chunkIndex.toString())

                    // Send the raw 1MB byte array
                    append("chunkData", chunk.data, Headers.build {
                        append(HttpHeaders.ContentType, "application/octet-stream")
                        append(HttpHeaders.ContentDisposition, "filename=\"chunk_${chunk.chunkIndex}.bin\"")
                    })
                }
            )
            response.status.isSuccess()
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}