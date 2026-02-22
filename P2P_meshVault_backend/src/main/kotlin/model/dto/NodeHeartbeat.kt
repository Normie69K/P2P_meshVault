package com.ltcoe.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class NodeHeartbeat(
    val nodeId: String,
    val timestamp: Long
)