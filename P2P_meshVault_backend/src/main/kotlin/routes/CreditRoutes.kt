package com.ltcoe.routes

import com.ltcoe.model.entity.AddCreditRequest
import com.ltcoe.model.entity.TransferCreditRequest
import com.ltcoe.service.CreditService
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable

@Serializable
data class BalanceResponse(val publicKey: String, val balance: Long)

fun Route.creditRoutes(creditService: CreditService) {
    route("/credits") {

        // 1. Check balance
        get("/{publicKey}") {
            val publicKey = call.parameters["publicKey"] ?: return@get call.respond(HttpStatusCode.BadRequest)
            val balance = creditService.getBalance(publicKey)
//          call.respond(HttpStatusCode.OK, mapOf("publicKey" to publicKey, "balance" to balance))
            call.respond(HttpStatusCode.OK, BalanceResponse(publicKey, balance))
        }

        // 2. Add test credits (like a crypto faucet)
        post("/faucet") {
            val request = call.receive<AddCreditRequest>()
            creditService.addTestCredits(request.publicKey, request.amount)
            call.respond(HttpStatusCode.OK, mapOf("message" to "Successfully added ${request.amount} credits to ${request.publicKey}"))
        }

        // 3. Transfer credits (User pays Node)
        post("/transfer") {
            val request = call.receive<TransferCreditRequest>()
            val success = creditService.processPayment(request.senderPublicKey, request.receiverPublicKey, request.amount)

            if (success) {
                call.respond(HttpStatusCode.OK, mapOf("message" to "Payment successful"))
            } else {
                call.respond(HttpStatusCode.PaymentRequired, mapOf("error" to "Insufficient funds"))
            }
        }
    }
}