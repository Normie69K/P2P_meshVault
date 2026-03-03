package routes // Or whatever your package is

import com.ltcoe.service.FileService
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.io.File

fun Route.fileRoutes(fileService: FileService) {
    route("/files") {

        post("/upload-chunk") {
            val multipartData = call.receiveMultipart()

            var fileId = ""
            var chunkIndex = -1
            var chunkBytes: ByteArray? = null

            multipartData.forEachPart { part ->
                when (part) {
                    is PartData.FormItem -> {
                        when (part.name) {
                            "fileId" -> fileId = part.value
                            "chunkIndex" -> chunkIndex = part.value.toIntOrNull() ?: -1
                        }
                    }
                    is PartData.FileItem -> {
                        if (part.name == "chunkData") {
                            chunkBytes = part.streamProvider().readBytes()
                        }
                    }
                    else -> {}
                }
                part.dispose()
            }

            if (fileId.isNotBlank() && chunkIndex != -1 && chunkBytes != null) {
                // Save the chunk to disk
                val fileDir = File("storage/chunks/$fileId")
                fileDir.mkdirs()

                val chunkFile = File(fileDir, "chunk_$chunkIndex.bin")
                chunkFile.writeBytes(chunkBytes!!)

                println("Received chunk $chunkIndex for file $fileId")

                // TODO later: You can call fileService.saveChunkData(fileId, chunkIndex) here!

                call.respond(HttpStatusCode.OK, mapOf("status" to "success"))
            } else {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Missing data"))
            }
        }
    }
}