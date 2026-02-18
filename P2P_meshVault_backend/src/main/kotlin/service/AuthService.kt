package com.ltcoe.service

import com.ltcoe.model.dto.RegisterRequest
import com.ltcoe.model.entity.User
import com.ltcoe.repository.UserRepository
import java.util.UUID

class AuthService(private val userRepository: UserRepository) {

    fun register(request: RegisterRequest): User {
        // Check if user already exists
        val existingUser = userRepository.findByPublicKey(request.publicKey)
        if (existingUser != null) {
            return existingUser // Or throw an exception if you prefer
        }

        // Create new user
        val newUser = User(
            userId = UUID.randomUUID().toString(),
            publicKey = request.publicKey
        )
        return userRepository.save(newUser)
    }
}