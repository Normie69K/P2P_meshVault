package com.ltcoe.model.entity

import kotlinx.serialization.Serializable

@Serializable
data class Node(
    val nodeId: String,
    val publicKey: String,
    val ipAddress: String,
    val port: Int,
    var lastHeartbeat: Long,
    var isActive: Boolean = true
)