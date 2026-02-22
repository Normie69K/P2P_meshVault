package com.ltcoe.service

import com.ltcoe.model.dto.RegisterRequest
import com.ltcoe.model.entity.User
import com.ltcoe.repository.UserRepository
import com.ltcoe.util.JwtProvider
import com.ltcoe.util.SignatureVerifier
import java.security.PublicKey
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

class AuthService(private val userRepository: UserRepository) {

    // Stores active challenges: Map<PublicKey, ChallengeString>
    private val activeChallenges = ConcurrentHashMap<String, String>()

    fun generateChallenges(publicKey: String): String {
        userRepository.findByPublicKey(publicKey) ?: throw IllegalArgumentException("User not registered: $publicKey")

        val challenge = UUID.randomUUID().toString()
        activeChallenges[publicKey] = challenge
        return challenge
    }


    fun login(publicKey: String, signature: String): String? {
        val user = userRepository.findByPublicKey(publicKey) ?: return null
        val challenge = activeChallenges[publicKey] ?: return null

        // Verify the signature against the stored challenge
        val isValid = SignatureVerifier.verifySignature(publicKey, signature, challenge)

        return if (isValid) {
            activeChallenges.remove(publicKey)  // Prevent replay attacks
            JwtProvider.generateToken(user.userId, user.publicKey)
        }else{
            null
        }
    }


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