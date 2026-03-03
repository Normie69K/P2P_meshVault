package com.ltcoe.meshvault.util

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class SecureStorageManager(context: Context) {

    // 1. Create a Master Key tied to the Android Hardware Keystore
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    // 2. Initialize the Encrypted File
    private val sharedPreferences = EncryptedSharedPreferences.create(
        context,
        "meshvault_secure_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    // Save the wallet
    fun saveWallet(privateKeyHex: String, publicKeyHex: String) {
        sharedPreferences.edit()
            .putString("PRIVATE_KEY", privateKeyHex)
            .putString("PUBLIC_KEY", publicKeyHex)
            .apply()
    }

    // Retrieve the wallet
    fun getPrivateKey(): String? = sharedPreferences.getString("PRIVATE_KEY", null)
    fun getPublicKey(): String? = sharedPreferences.getString("PUBLIC_KEY", null)

    // Check if a wallet exists
    fun hasWallet(): Boolean = getPrivateKey() != null

    // Wipe the wallet (for the Settings -> Disconnect button later)
    fun clearWallet() {
        sharedPreferences.edit().clear().apply()
    }
}