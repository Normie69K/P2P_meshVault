package com.ltcoe.util

import java.security.KeyFactory
import java.security.Signature
import java.security.spec.X509EncodedKeySpec
import java.util.Base64

object SignatureVerifier {
    fun verifySignature(publicKeyBase64: String, message: String, signatureBase64: String): Boolean {
        return try {
            val publicBytes = Base64.getDecoder().decode(publicKeyBase64)
            val signatureBytes = Base64.getDecoder().decode(signatureBase64)

            // Ed25519 is the standard for modern blockchains
            val keyFactory = KeyFactory.getInstance("Ed25519")
            val publicKey = keyFactory.generatePublic(X509EncodedKeySpec(publicBytes))

            val signature = Signature.getInstance("Ed25519")
            signature.initVerify(publicKey)
            signature.update(message.toByteArray())

            signature.verify(signatureBytes)
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}