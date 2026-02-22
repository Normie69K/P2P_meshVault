package com.ltcoe.model.entity

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table

@Serializable
data class Node(
    val nodeId: String,
    val publicKey: String,
    val ipAddress: String,
    val port: Int,
    var lastHeartbeat: Long,
    var isActive: Boolean = true
)

object Nodes : Table("nodes") {
    val nodeId = varchar("node_id", 36)
    val publicKey = varchar("public_key", 255)
    val ipAddress = varchar("ip_address", 45)
    val port = integer("port")
    val lastHeartbeat = long("last_heartbeat")
    val isActive = bool("is_active")

    override val primaryKey = PrimaryKey(nodeId)
}