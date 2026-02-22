package com.ltcoe

import com.ltcoe.config.DatabaseConfig
import com.ltcoe.repository.CreditRepository
import com.ltcoe.repository.FileRepository
import com.ltcoe.repository.NodeRepository
import com.ltcoe.repository.UserRepository
import com.ltcoe.routes.authRoutes
import com.ltcoe.routes.creditRoutes
import com.ltcoe.routes.fileRoutes
import com.ltcoe.routes.nodeRoutes
import com.ltcoe.service.AuthService
import com.ltcoe.service.CreditService
import com.ltcoe.service.FileService
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

    val dbUrl = environment.config.property("database.url").getString()
    val dbUser = environment.config.property("database.user").getString()
    val dbPassword = environment.config.property("database.password").getString()

    DatabaseConfig.init(dbUrl, dbUser, dbPassword)

    install(CallLogging)
    install(ContentNegotiation) {
        json()
    }
    //Repository
    val userRepository = UserRepository()
    val nodeRepository = NodeRepository()
    val fileRepository = FileRepository()
    val creditRepository = CreditRepository()

    //Service
    val authService = AuthService(userRepository)
    val nodeService = NodeService(nodeRepository)
    val fileService = FileService(fileRepository , nodeService)
    val creditService = CreditService(creditRepository)

    // 2. Register Routes
    routing {
        authRoutes(authService)
        nodeRoutes(nodeService)
        fileRoutes(fileService)
        creditRoutes(creditService)
    }
}