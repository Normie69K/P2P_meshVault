package com.ltcoe.service

import com.ltcoe.model.entity.FileMetadata
import com.ltcoe.repository.FileRepository
import java.util.UUID

class FileService(
    private val fileRepository: FileRepository,
    private val nodeService: NodeService // We inject this to check for active nodes!
) {

    fun initiateUpload(ownerPublicKey: String, fileName: String, fileSize: Long, totalChunks: Int): FileMetadata {
        val activeNodes = nodeService.getActiveNodes()

        if (activeNodes.isEmpty()) {
            throw IllegalStateException("No active nodes available in the network to store files.")
        }

        // Simple Round-Robin distribution: assign chunks to nodes evenly
        val chunkMap = mutableMapOf<Int, String>()
        for (i in 0 until totalChunks) {
            val assignedNode = activeNodes[i % activeNodes.size]
            chunkMap[i] = assignedNode.nodeId
        }

        val metadata = FileMetadata(
            fileId = UUID.randomUUID().toString(),
            ownerPublicKey = ownerPublicKey,
            fileName = fileName,
            fileSize = fileSize,
            chunkMap = chunkMap,
            createdAt = System.currentTimeMillis()
        )

        return fileRepository.save(metadata)
    }

    fun getFile(fileId: String): FileMetadata? {
        return fileRepository.findById(fileId)
    }

    fun getUserFiles(publicKey: String): List<FileMetadata> {
        return fileRepository.findByOwner(publicKey)
    }
}