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
            lastHeartbeat = System.currentTimeMillis(),
            isActive = true
        )

        return nodeRepository.register(newNode)
    }

    fun processHeartbeat(heartbeat: NodeHeartbeat): Boolean {
        nodeRepository.updateHeartbeat(heartbeat.nodeId, System.currentTimeMillis())
        return true
    }

    fun getActiveNodes(): List<Node> {
        val fiveMinutesInMs = 5 * 60 * 1000L
        return nodeRepository.getActiveNodes(fiveMinutesInMs)
    }
}