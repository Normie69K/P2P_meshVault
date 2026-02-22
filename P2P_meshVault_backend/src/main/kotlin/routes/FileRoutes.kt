package com.ltcoe.routes

import com.ltcoe.service.FileService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable

@Serializable
data class InitiateUploadRequest(val publicKey: String, val fileName: String, val fileSize: Long, val totalChunks: Int)

fun Route.fileRoutes(fileService: FileService) {
    route("/files") {

        // 1. Client asks: "Where do I upload these chunks?"
        post("/initiate") {
            val request = try {
                call.receive<InitiateUploadRequest>()
            } catch (e: Exception) {
                return@post call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid request payload"))
            }

            try {
                val metadata = fileService.initiateUpload(
                    request.publicKey,
                    request.fileName,
                    request.fileSize,
                    request.totalChunks
                )
                call.respond(HttpStatusCode.Created, metadata)
            } catch (e: Exception) {
                // Catches the "No active nodes" error
                call.respond(HttpStatusCode.ServiceUnavailable, mapOf("error" to e.message))
            }
        }

        // 2. Client asks: "Where is my file located so I can download it?"
        get("/{id}") {
            val id = call.parameters["id"] ?: return@get call.respond(HttpStatusCode.BadRequest)
            val metadata = fileService.getFile(id)

            if (metadata != null) {
                call.respond(HttpStatusCode.OK, metadata)
            } else {
                call.respond(HttpStatusCode.NotFound, mapOf("error" to "File not found"))
            }
        }

        // 3. Get all files for a specific user
        get("/user/{publicKey}") {
            val publicKey = call.parameters["publicKey"] ?: return@get call.respond(HttpStatusCode.BadRequest)
            val files = fileService.getUserFiles(publicKey)
            call.respond(HttpStatusCode.OK, files)
        }
    }
}