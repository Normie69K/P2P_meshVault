package com.ltcoe.repository

import com.ltcoe.model.entity.Credits
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

class CreditRepository {

    fun getBalance(publicKey: String): Long {
        return transaction {
            Credits.select { Credits.publicKey eq publicKey }
                .map { it[Credits.balance] }
                .singleOrNull() ?: 0L
        }
    }

    fun addBalance(publicKey: String, amount: Long) {
        transaction {
            val existingBalance = getBalance(publicKey)
            if (existingBalance == 0L && Credits.select { Credits.publicKey eq publicKey }.empty()) {
                // User doesn't exist in the credit table yet, insert them
                Credits.insert {
                    it[this.publicKey] = publicKey
                    it[balance] = amount
                }
            } else {
                // User exists, update their balance
                Credits.update({ Credits.publicKey eq publicKey }) {
                    it[balance] = existingBalance + amount
                }
            }
        }
    }

    fun deductBalance(publicKey: String, amount: Long): Boolean {
        return transaction {
            val existingBalance = getBalance(publicKey)
            if (existingBalance >= amount) {
                Credits.update({ Credits.publicKey eq publicKey }) {
                    it[balance] = existingBalance - amount
                }
                true
            } else {
                false
            }
        }
    }
}