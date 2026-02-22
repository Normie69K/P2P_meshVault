package com.ltcoe.routes

import com.ltcoe.model.dto.RegisterRequest
import com.ltcoe.service.AuthService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlin.collections.mapOf

fun Route.authRoutes(authService: AuthService) {
    route("/auth") {

        post("/register") {
            // Receive the JSON body as RegisterRequest
            val request = call.receive<RegisterRequest>()
            try {
                val user = authService.register(request)
                call.respond(HttpStatusCode.Created, user)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, "Registration failed")
            }
        }

        get("/challenge/{publicKey}") {
            val publicKey = call.parameters["publicKey"] ?: return@get call.respond(HttpStatusCode.BadRequest)
            try {
                val challengeStr = authService.generateChallenges(publicKey)
                call.respond(HttpStatusCode.OK, mapOf("challenge" to challengeStr))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.NotFound, mapOf("error" to "User not found"))
            }
        }

        post("/login") {
            val request = call.receive<com.ltcoe.model.dto.LoginRequest>()
            val token = authService.login(request.publicKey, request.signature)

            if (token != null) {
                call.respond(HttpStatusCode.OK, mapOf("token" to token))
            } else {
                call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Invalid signature or challenge expired"))
            }
        }

    }
}