package com.ltcoe.model.entity

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table

@Serializable
data class User(
    val userId: String,
    val publicKey: String
)

object Users : Table("users") {
    val userId = varchar("user_id", 36)
    val publicKey = varchar("public_key", 255).uniqueIndex()
    override val primaryKey = PrimaryKey(userId)
}