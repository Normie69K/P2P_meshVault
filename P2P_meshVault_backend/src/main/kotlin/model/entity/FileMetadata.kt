package com.ltcoe.model.entity

import kotlinx.serialization.Serializable

@Serializable
data class FileMetadata(
    val fileId: String,
    val ownerPublicKey: String,
    val fileName: String,
    val fileSize: Long,
    // A map linking Chunk Index (e.g., chunk 0) to a Node ID
    val chunkMap: Map<Int, String>,
    val createdAt: Long
)