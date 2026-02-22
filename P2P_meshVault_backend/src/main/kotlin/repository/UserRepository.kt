package com.ltcoe.repository

import com.ltcoe.model.entity.User
import com.ltcoe.model.entity.Users
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

class UserRepository {

    fun save(user: User): User {
        transaction {
            Users.insert {
                it[userId] = user.userId
                it[publicKey] = user.publicKey
            }
        }
        return user
    }

    fun findByPublicKey(publicKey: String): User? {
        return transaction {
            Users.select { Users.publicKey eq publicKey }
                .map { row ->
                    User(
                        userId = row[Users.userId],
                        publicKey = row[Users.publicKey]
                    )
                }
                .singleOrNull()
        }
    }

    fun count(): Int {
        return transaction {
            Users.selectAll().count().toInt()
        }
    }
}