package com.ltcoe.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequest(
    val publicKey: String
)
