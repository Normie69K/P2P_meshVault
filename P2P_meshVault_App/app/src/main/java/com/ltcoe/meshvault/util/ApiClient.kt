package com.ltcoe.meshvault.util

import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.isSuccess
import org.json.JSONArray

// NEW: Data class to hold the files fetched from Arch Linux
data class DashboardFile(val id: String, val name: String, val details: String)

object ApiClient {
    private const val BASE_URL = "https://p2p-meshvault.onrender.com"
    private val client = HttpClient(Android)

    // 1. Updated Upload function to send the Title
    suspend fun uploadChunk(chunk: FileChunk, fileTitle: String): Boolean {
        return try {
            val response = client.submitFormWithBinaryData(
                url = "$BASE_URL/files/upload-chunk",
                formData = formData {
                    append("fileId", chunk.fileId)
                    append("fileTitle", fileTitle) // <-- Send the title!
                    append("chunkIndex", chunk.chunkIndex.toString())
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

    // 2. NEW: Fetch real files for the Dashboard
    suspend fun getMyFiles(): List<DashboardFile> {
        val fileList = mutableListOf<DashboardFile>()
        try {
            val response = client.get("$BASE_URL/files/list")
            if (response.status.isSuccess()) {
                val jsonString = response.bodyAsText()
                val jsonArray = JSONArray(jsonString)

                for (i in 0 until jsonArray.length()) {
                    val obj = jsonArray.getJSONObject(i)
                    fileList.add(DashboardFile(
                        id = obj.getString("id"),
                        name = obj.getString("name"),
                        details = obj.getString("details")
                    ))
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return fileList // Return the real files!
    }
}