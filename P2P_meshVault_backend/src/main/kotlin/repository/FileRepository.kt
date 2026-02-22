package com.ltcoe.repository

import com.ltcoe.model.entity.FileMetadata

class FileRepository {
    private val files = mutableListOf<FileMetadata>()

    fun save(metadata: FileMetadata): FileMetadata {
        files.add(metadata)
        return metadata
    }

    fun findById(fileId: String): FileMetadata? {
        return files.find { it.fileId == fileId }
    }

    fun findByOwner(publicKey: String): List<FileMetadata> {
        return files.filter { it.ownerPublicKey == publicKey }
    }
}