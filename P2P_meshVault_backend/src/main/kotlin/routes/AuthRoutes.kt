package com.ltcoe.routes

import com.ltcoe.model.dto.RegisterRequest
import com.ltcoe.service.AuthService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

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
    }
}