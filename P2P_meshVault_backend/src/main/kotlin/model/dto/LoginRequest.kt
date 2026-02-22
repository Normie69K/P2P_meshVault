package com.ltcoe.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val publicKey: String,
    val signature: String // The challenge signed by the user's private key
)