package com.ltcoe.repository

class CreditRepository {
    // Maps a PublicKey to their Credit Balance
    private val balances = mutableMapOf<String, Long>()

    fun getBalance(publicKey: String): Long {
        return balances.getOrDefault(publicKey, 0L)
    }

    fun addBalance(publicKey: String, amount: Long) {
        val currentBalance = getBalance(publicKey)
        balances[publicKey] = currentBalance + amount
    }

    fun deductBalance(publicKey: String, amount: Long): Boolean {
        val currentBalance = getBalance(publicKey)
        if (currentBalance >= amount) {
            balances[publicKey] = currentBalance - amount
            return true
        }
        return false // Insufficient funds
    }
}