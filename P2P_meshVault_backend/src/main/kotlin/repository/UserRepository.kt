package com.ltcoe.repository

import com.ltcoe.model.entity.User

class UserRepository {
    // specific implementation can be replaced with DB later
    private val users = mutableListOf<User>()

    fun save(user: User): User {
        users.add(user)
        return user
    }

    fun findByPublicKey(publicKey: String): User? {
        return users.find { it.publicKey == publicKey }
    }
}