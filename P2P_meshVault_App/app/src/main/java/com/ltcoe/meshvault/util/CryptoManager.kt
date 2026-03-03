package com.ltcoe.meshvault.util

import java.security.KeyPairGenerator
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

// We need this data class to hold the generated Web3-style keys
data class WalletKeys(
    val privateKeyHex: String,
    val publicKeyHex: String
)

object CryptoManager {
    private const val TRANSFORMATION = "AES/GCM/NoPadding"
    private const val GCM_TAG_LENGTH = 128

    // --- 1. WALLET IDENTITY (For LoginScreen) ---
    /**
     * Generates a new Elliptic Curve (EC) Master Wallet KeyPair.
     */
    fun createNewWallet(): WalletKeys {
        val keyPairGen = KeyPairGenerator.getInstance("EC")
        keyPairGen.initialize(256, SecureRandom())
        val keyPair = keyPairGen.generateKeyPair()

        // Convert the raw bytes to Hexadecimal strings so they can be saved/displayed
        val privHex = keyPair.private.encoded.joinToString("") { "%02x".format(it) }
        val pubHex = keyPair.public.encoded.joinToString("") { "%02x".format(it) }

        return WalletKeys(privateKeyHex = privHex, publicKeyHex = pubHex)
    }

    // --- 2. FILE ENCRYPTION (For UploadScreen) ---
    /**
     * Generates a unique AES-256 key for a specific file.
     */
    fun generateFileKey(): SecretKey {
        val keyGen = KeyGenerator.getInstance("AES")
        keyGen.init(256)
        return keyGen.generateKey()
    }

    /**
     * Mathematically locks a 1MB file chunk so no one on the network can read it.
     */
    fun encryptChunk(rawChunk: ByteArray, secretKey: SecretKey): ByteArray {
        val cipher = Cipher.getInstance(TRANSFORMATION)

        val iv = ByteArray(12)
        SecureRandom().nextBytes(iv)

        val parameterSpec = GCMParameterSpec(GCM_TAG_LENGTH, iv)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, parameterSpec)

        val encryptedBytes = cipher.doFinal(rawChunk)

        return iv + encryptedBytes
    }
}