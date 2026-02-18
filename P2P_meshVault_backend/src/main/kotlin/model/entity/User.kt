package com.ltcoe.model.entity

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val userId: String,
    val publicKey: String
)