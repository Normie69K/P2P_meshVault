package com.ltcoe.repository

import com.ltcoe.model.entity.FileMetadata
import com.ltcoe.model.entity.Files
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

class FileRepository {

    val fileRepository = null

    fun save(metadata: FileMetadata): FileMetadata {
        val mapAsJson = Json.encodeToString(metadata.chunkMap)

        transaction {
            Files.insert {
                it[fileId] = metadata.fileId
                it[ownerPublicKey] = metadata.ownerPublicKey
                it[fileName] = metadata.fileName
                it[fileSize] = metadata.fileSize
                it[chunkMap] = mapAsJson
                it[createdAt] = metadata.createdAt
            }
        }
        return metadata
    }

    fun deleteById(fileId: String) {
        transaction {
            Files.deleteWhere { Files.fileId eq fileId }
        }
    }

    fun findExpiredFiles(currentTime: Long): List<FileMetadata> {
        return transaction {
            // Note: This requires you to add 'expirationTime' to your Files.kt entity
            // Files.select { Files.expirationTime less currentTime }.map { ... }

            // For now, returning empty list so it compiles until you update your DB Table
            emptyList()
        }
    }

    fun findByOwner(publicKey: String): List<FileMetadata> {
        return transaction {
            Files.select { Files.ownerPublicKey eq publicKey }.map { row ->
                FileMetadata(
                    fileId = row[Files.fileId],
                    ownerPublicKey = row[Files.ownerPublicKey],
                    fileName = row[Files.fileName],
                    fileSize = row[Files.fileSize],
                    chunkMap = emptyMap(),
                    createdAt = row[Files.createdAt]
                )
            }
        }
    }
}