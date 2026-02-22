package com.ltcoe.util

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import java.util.Date

object JwtProvider {
    private const val SECRET = "your-super-secret-key-change-this-in-production"
    private const val ISSUER = "ltcoe-network"
    private const val VALIDITY_IN_MS = 36_000_00 * 24 // 24 hours

    fun generateToken(userId: String, publicKey: String): String {
        return JWT.create()
            .withIssuer(ISSUER)
            .withClaim("userId", userId)
            .withClaim("publicKey", publicKey)
            .withExpiresAt(Date(System.currentTimeMillis() + VALIDITY_IN_MS))
            .sign(Algorithm.HMAC256(SECRET))
    }
}