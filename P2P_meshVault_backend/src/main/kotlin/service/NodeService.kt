package com.ltcoe.service

import com.ltcoe.model.dto.NodeHeartbeat
import com.ltcoe.model.entity.Node
import com.ltcoe.repository.NodeRepository
import java.util.UUID

class NodeService(private val nodeRepository: NodeRepository) {

    fun registerNode(publicKey: String, ipAddress: String, port: Int): Node {
        val newNode = Node(
            nodeId = UUID.randomUUID().toString(),
            publicKey = publicKey,
            ipAddress = ipAddress,
            port = port,
            lastHeartbeat = System.currentTimeMillis()
        )
        return nodeRepository.save(newNode)
    }

    fun processHeartbeat(heartbeat: NodeHeartbeat): Boolean {
        val node = nodeRepository.findById(heartbeat.nodeId) ?: return false

        // Update the last heartbeat timestamp
        node.lastHeartbeat = System.currentTimeMillis()
        node.isActive = true
        nodeRepository.save(node)

        return true
    }

    fun getActiveNodes(): List<Node> {
        return nodeRepository.getAllActive()
    }
}