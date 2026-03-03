package routes

import com.ltcoe.service.FileService
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.io.File

fun Route.fileRoutes(fileService: FileService) {
    route("/files") {

        post("/upload-chunk") {
            val multipartData = call.receiveMultipart()

            var fileId = ""
            var fileTitle = "Encrypted_File" // Default title
            var chunkIndex = -1
            var chunkBytes: ByteArray? = null

            multipartData.forEachPart { part ->
                when (part) {
                    is PartData.FormItem -> {
                        when (part.name) {
                            "fileId" -> fileId = part.value
                            "fileTitle" -> fileTitle = part.value // <-- Read the title from Android!
                            "chunkIndex" -> chunkIndex = part.value.toIntOrNull() ?: -1
                        }
                    }
                    is PartData.FileItem -> {
                        if (part.name == "chunkData") chunkBytes = part.streamProvider().readBytes()
                    }
                    else -> {}
                }
                part.dispose()
            }

            if (fileId.isNotBlank() && chunkIndex != -1 && chunkBytes != null) {
                val fileDir = File("storage/chunks/$fileId")
                fileDir.mkdirs()

                // Save the actual AES Encrypted chunk
                val chunkFile = File(fileDir, "chunk_$chunkIndex.bin")
                chunkFile.writeBytes(chunkBytes!!)

                // NEW: Save the File Title as a tiny metadata text file for the Dashboard
                val titleFile = File(fileDir, "metadata.txt")
                if (!titleFile.exists()) titleFile.writeText(fileTitle)

                println("Received chunk $chunkIndex for file $fileId ($fileTitle)")
                call.respond(HttpStatusCode.OK, mapOf("status" to "success"))
            } else {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Missing data"))
            }
        }

        // NEW: Endpoint to get all files for the Android Dashboard!
        get("/list") {
            val storageDir = File("storage/chunks")
            val filesList = mutableListOf<Map<String, String>>()

            if (storageDir.exists()) {
                storageDir.listFiles()?.forEach { fileDir ->
                    if (fileDir.isDirectory) {
                        // Count the 1MB chunks to calculate the file size
                        val chunks = fileDir.listFiles()?.count { it.name.startsWith("chunk_") } ?: 0

                        // Read the title we saved earlier
                        val titleFile = File(fileDir, "metadata.txt")
                        val title = if (titleFile.exists()) titleFile.readText() else "Unknown Vault File"

                        filesList.add(mapOf(
                            "id" to fileDir.name,
                            "name" to title,
                            "details" to "$chunks MB • AES-256 Encrypted"
                        ))
                    }
                }
            }
            call.respond(HttpStatusCode.OK, filesList) // Sends data as JSON!
        }
    }
}