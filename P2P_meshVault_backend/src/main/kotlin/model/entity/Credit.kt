package com.ltcoe.model.entity

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table

// For API requests
@Serializable
data class AddCreditRequest(val publicKey: String, val amount: Long)

@Serializable
data class TransferCreditRequest(val senderPublicKey: String, val receiverPublicKey: String, val amount: Long)

object Credits : Table("credits") {
    val publicKey = varchar("public_key", 255)
    val balance = long("balance")

    override val primaryKey = PrimaryKey(publicKey)
}