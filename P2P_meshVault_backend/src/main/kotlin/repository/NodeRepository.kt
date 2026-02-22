package com.ltcoe.repository

import com.ltcoe.model.entity.Node
import com.ltcoe.model.entity.Nodes
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

class NodeRepository {

    fun register(node: Node): Node {
        transaction {
            Nodes.insert {
                it[nodeId] = node.nodeId
                it[publicKey] = node.publicKey
                it[ipAddress] = node.ipAddress
                it[port] = node.port
                it[lastHeartbeat] = node.lastHeartbeat
                it[isActive] = node.isActive
            }
        }
        return node
    }

    fun updateHeartbeat(nodeId: String, timestamp: Long) {
        transaction {
            Nodes.update({ Nodes.nodeId eq nodeId }) {
                it[lastHeartbeat] = timestamp
                it[isActive] = true
            }
        }
    }

    fun getActiveNodes(timeoutMs: Long): List<Node> {
        val cutoffTime = System.currentTimeMillis() - timeoutMs
        return transaction {
            // First, mark nodes as inactive if they missed their heartbeat
            Nodes.update({ Nodes.lastHeartbeat less cutoffTime }) {
                it[isActive] = false
            }

            // Then, fetch only the active ones
            Nodes.select { Nodes.isActive eq true }.map { row ->
                Node(
                    nodeId = row[Nodes.nodeId],
                    publicKey = row[Nodes.publicKey],
                    ipAddress = row[Nodes.ipAddress],
                    port = row[Nodes.port],
                    lastHeartbeat = row[Nodes.lastHeartbeat],
                    isActive = row[Nodes.isActive]
                )
            }
        }
    }
}