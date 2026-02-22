package com.ltcoe.model.entity

import kotlinx.serialization.Serializable

// For API requests
@Serializable
data class AddCreditRequest(val publicKey: String, val amount: Long)

@Serializable
data class TransferCreditRequest(val senderPublicKey: String, val receiverPublicKey: String, val amount: Long)