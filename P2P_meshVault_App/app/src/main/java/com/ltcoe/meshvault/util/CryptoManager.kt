package com.ltcoe.meshvault.util

import org.bouncycastle.crypto.generators.Ed25519KeyPairGenerator
import org.bouncycastle.crypto.params.Ed25519KeyGenerationParameters
import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters
import org.bouncycastle.crypto.params.Ed25519PublicKeyParameters
import java.security.SecureRandom

// A simple data class to hold our newly created identity
data class VaultWallet(
    val privateKeyHex: String, // The Master Key (Keep Secret)
    val publicKeyHex: String   // The Node Address (Public)
)

object CryptoManager {

    /**
     * Generates a mathematically secure Ed25519 Key Pair
     */
    fun createNewWallet(): VaultWallet {
        val random = SecureRandom()
        val generator = Ed25519KeyPairGenerator()

        // Initialize the generator with secure randomness
        generator.init(Ed25519KeyGenerationParameters(random))

        // Generate the raw keys
        val keyPair = generator.generateKeyPair()
        val privateKey = keyPair.private as Ed25519PrivateKeyParameters
        val publicKey = keyPair.public as Ed25519PublicKeyParameters

        // Convert the raw bytes to Hexadecimal strings (Web3 standard format)
        return VaultWallet(
            privateKeyHex = privateKey.encoded.toHexString(),
            publicKeyHex = publicKey.encoded.toHexString()
        )
    }

    // Helper function to convert Byte Arrays to Hex Strings
    private fun ByteArray.toHexString(): String {
        return joinToString("") { "%02x".format(it) }
    }
}