package com.ltcoe.meshvault.util

import android.content.Context
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object MeshNodeServer {
    private var server: ApplicationEngine? = null
    var isRunning = false

    var maxStorageAllocatedBytes: Long = 500L * 1024 * 1024
    var currentStorageUsedBytes: Long = 0L

    suspend fun startServer(context: Context, port: Int = 8080) = withContext(Dispatchers.IO) {
        if (server != null) return@withContext

        server = embeddedServer(CIO, port = port) {
            routing {
                get("/ping") {
                    call.respondText("MeshVault Node is Online on Android!")
                }

                // The future P2P Receiver Route
                post("/p2p/receive-chunk") {
                    // STORAGE LIMIT CHECK
                    if (currentStorageUsedBytes >= maxStorageAllocatedBytes) {
                        call.respond(HttpStatusCode.InsufficientStorage, "Node storage limit reached")
                        return@post
                    }

                    // Logic to accept and save the encrypted chunk goes here...
                    call.respond(HttpStatusCode.OK, "Chunk Accepted")
                }
            }
        }.start(wait = false)

        isRunning = true

        MeshNetworkManager.init(context)
        MeshNetworkManager.startBroadcasting(port)
        MeshNetworkManager.startDiscovery()
    }

    suspend fun stopServer() = withContext(Dispatchers.IO) {
        server?.stop(1000, 2000)
        server = null
        isRunning = false

        MeshNetworkManager.stopBroadcasting()
        MeshNetworkManager.stopDiscovery()
    }

    suspend fun getLocalIpAddress(): String = withContext(Dispatchers.IO) {
        try {
            val interfaces = java.net.NetworkInterface.getNetworkInterfaces()
            for (intf in interfaces) {
                val addrs = intf.inetAddresses
                for (addr in addrs) {
                    if (!addr.isLoopbackAddress && addr is java.net.Inet4Address) {
                        return@withContext addr.hostAddress ?: "Unknown"
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return@withContext "127.0.0.1"
    }
}