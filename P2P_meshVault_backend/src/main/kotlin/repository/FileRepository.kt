package com.ltcoe.repository

import com.ltcoe.model.entity.FileMetadata
import com.ltcoe.model.entity.Files
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

class FileRepository {

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

    fun findById(fileId: String): FileMetadata? {
        return transaction {
            Files.select { Files.fileId eq fileId }.map { row ->
                FileMetadata(
                    fileId = row[Files.fileId],
                    ownerPublicKey = row[Files.ownerPublicKey],
                    fileName = row[Files.fileName],
                    fileSize = row[Files.fileSize],
                    chunkMap = Json.decodeFromString(row[Files.chunkMap]),
                    createdAt = row[Files.createdAt]
                )
            }.singleOrNull()
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
                    chunkMap = Json.decodeFromString(row[Files.chunkMap]),
                    createdAt = row[Files.createdAt]
                )
            }
        }
    }
}