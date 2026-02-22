package com.ltcoe.service

import com.ltcoe.repository.CreditRepository

class CreditService(private val creditRepository: CreditRepository) {

    fun getBalance(publicKey: String): Long {
        return creditRepository.getBalance(publicKey)
    }

    // A "faucet" function to give free test credits to new users
    fun addTestCredits(publicKey: String, amount: Long) {
        require(amount > 0) { "Amount must be positive" }
        creditRepository.addBalance(publicKey, amount)
    }

    // Process a payment (e.g., User pays a Node for storage)
    fun processPayment(senderPublicKey: String, receiverPublicKey: String, amount: Long): Boolean {
        require(amount > 0) { "Payment amount must be positive" }

        // Try to deduct from sender. If successful, add to receiver.
        val deductionSuccessful = creditRepository.deductBalance(senderPublicKey, amount)
        if (deductionSuccessful) {
            creditRepository.addBalance(receiverPublicKey, amount)
            return true
        }
        return false // Payment failed (Not enough credits)
    }
}