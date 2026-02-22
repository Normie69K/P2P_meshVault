package com.ltcoe.model.entity

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table

@Serializable
data class FileMetadata(
    val fileId: String,
    val ownerPublicKey: String,
    val fileName: String,
    val fileSize: Long,
    val chunkMap: Map<Int, String>,
    val createdAt: Long
)

object Files : Table("files") {
    val fileId = varchar("file_id", 36)
    val ownerPublicKey = varchar("owner_public_key", 255)
    val fileName = varchar("file_name", 255)
    val fileSize = long("file_size")
    val chunkMap = text("chunk_map") // We will store the map as a JSON String
    val createdAt = long("created_at")

    override val primaryKey = PrimaryKey(fileId)
}