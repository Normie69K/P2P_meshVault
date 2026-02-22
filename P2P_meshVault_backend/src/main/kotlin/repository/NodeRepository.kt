package com.ltcoe.repository

import com.ltcoe.model.entity.Node

class NodeRepository {
    private val nodes = mutableListOf<Node>()

    fun save(node: Node): Node {
        // Remove existing node with same ID if it exists (update)
        nodes.removeIf { it.nodeId == node.nodeId }
        nodes.add(node)
        return node
    }

    fun findById(nodeId: String): Node? {
        return nodes.find { it.nodeId == nodeId }
    }

    fun getAllActive(): List<Node> {
        return nodes.filter { it.isActive }
    }
}