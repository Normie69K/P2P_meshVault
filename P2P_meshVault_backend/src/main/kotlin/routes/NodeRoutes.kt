package com.ltcoe.routes

import com.ltcoe.model.dto.NodeHeartbeat
import com.ltcoe.model.entity.Node
import com.ltcoe.service.NodeService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable

// Temporary DTO just for receiving the registration payload
@Serializable
data class RegisterNodeRequest(val publicKey: String, val ipAddress: String, val port: Int)

fun Route.nodeRoutes(nodeService: NodeService) {
    route("/nodes") {

        // 1. Register a new node
        post("/register") {
            val request = call.receive<RegisterNodeRequest>()
            val node = nodeService.registerNode(request.publicKey, request.ipAddress, request.port)
            call.respond(HttpStatusCode.Created, node)
        }

        // 2. Receive heartbeat from a node
        post("/heartbeat") {
            val heartbeat = call.receive<NodeHeartbeat>()
            val success = nodeService.processHeartbeat(heartbeat)

            if (success) {
                call.respond(HttpStatusCode.OK, mapOf("status" to "Heartbeat acknowledged"))
            } else {
                call.respond(HttpStatusCode.NotFound, mapOf("error" to "Node not found"))
            }
        }

        // 3. Get all active nodes (useful for clients looking for a node to upload files to)
        get("/active") {
            val activeNodes = nodeService.getActiveNodes()
            call.respond(HttpStatusCode.OK, activeNodes)
        }
    }
}