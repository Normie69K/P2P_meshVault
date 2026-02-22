package com.ltcoe

import com.ltcoe.repository.NodeRepository
import com.ltcoe.repository.UserRepository
import com.ltcoe.routes.authRoutes
import com.ltcoe.routes.nodeRoutes
import com.ltcoe.service.AuthService
import com.ltcoe.service.NodeService
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.plugins.calllogging.CallLogging
import io.ktor.server.routing.*

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        module()
    }.start(wait = true)
}

fun Application.module() {
    install(CallLogging)
    install(ContentNegotiation) {
        json()
    }
    //Repository
    val userRepository = UserRepository()
    val nodeRepository = NodeRepository()

    //Service
    val authService = AuthService(userRepository)
    val nodeService = NodeService(nodeRepository)

    // 2. Register Routes
    routing {
        authRoutes(authService)
        nodeRoutes(nodeService)
    }
}