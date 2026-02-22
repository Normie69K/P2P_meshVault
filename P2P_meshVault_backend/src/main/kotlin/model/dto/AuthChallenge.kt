package com.ltcoe.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class AuthChallenge(
    val publicKey: String,
    val challenge: String
)