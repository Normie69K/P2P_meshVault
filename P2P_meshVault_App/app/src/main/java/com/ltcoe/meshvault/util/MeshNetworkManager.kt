package com.ltcoe.meshvault.util

import android.content.Context
import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import android.os.Handler
import android.os.Looper
import androidx.compose.runtime.mutableStateListOf

// Represents a real physical phone discovered on the Wi-Fi
data class MeshNode(val id: String, val ip: String, val port: Int)

object MeshNetworkManager {
    // This is the secret frequency our app uses to find other phones
    private const val SERVICE_TYPE = "_meshvault._tcp."
    private const val SERVICE_NAME = "MeshVaultNode"

    private var nsdManager: NsdManager? = null
    private var registrationListener: NsdManager.RegistrationListener? = null
    private var discoveryListener: NsdManager.DiscoveryListener? = null

    // The LIVE list of real phones on the network. Compose will automatically redraw when this changes!
    val activeNodes = mutableStateListOf<MeshNode>()
    private val mainHandler = Handler(Looper.getMainLooper()) // Safely updates UI from background threads

    fun init(context: Context) {
        if (nsdManager == null) {
            nsdManager = context.getSystemService(Context.NSD_SERVICE) as NsdManager
        }
    }

    // 1. Shout to the router: "I am a server!"
    fun startBroadcasting(port: Int) {
        val serviceInfo = NsdServiceInfo().apply {
            // Give this phone a unique ID so we don't confuse it with others
            serviceName = "$SERVICE_NAME-${System.currentTimeMillis().toString().takeLast(5)}"
            serviceType = SERVICE_TYPE
            setPort(port)
        }

        registrationListener = object : NsdManager.RegistrationListener {
            override fun onServiceRegistered(NsdServiceInfo: NsdServiceInfo) {}
            override fun onRegistrationFailed(serviceInfo: NsdServiceInfo, errorCode: Int) {}
            override fun onServiceUnregistered(arg0: NsdServiceInfo) {}
            override fun onUnregistrationFailed(serviceInfo: NsdServiceInfo, errorCode: Int) {}
        }

        nsdManager?.registerService(serviceInfo, NsdManager.PROTOCOL_DNS_SD, registrationListener)
    }

    fun stopBroadcasting() {
        registrationListener?.let { nsdManager?.unregisterService(it) }
        registrationListener = null
    }

    // 2. Listen to the router: "Is anyone else a server?"
    fun startDiscovery() {
        mainHandler.post { activeNodes.clear() }

        discoveryListener = object : NsdManager.DiscoveryListener {
            override fun onDiscoveryStarted(regType: String) {}

            override fun onServiceFound(service: NsdServiceInfo) {
                // We found a signal! Now we ask the router for its exact IP address.
                if (service.serviceType == SERVICE_TYPE) {
                    nsdManager?.resolveService(service, object : NsdManager.ResolveListener {
                        override fun onServiceResolved(serviceInfo: NsdServiceInfo) {
                            val ip = serviceInfo.host.hostAddress ?: return
                            val port = serviceInfo.port
                            val node = MeshNode(serviceInfo.serviceName, ip, port)

                            // Add the real phone to our UI list
                            mainHandler.post {
                                if (!activeNodes.any { it.ip == ip }) {
                                    activeNodes.add(node)
                                }
                            }
                        }
                        override fun onResolveFailed(serviceInfo: NsdServiceInfo, errorCode: Int) {}
                    })
                }
            }

            override fun onServiceLost(service: NsdServiceInfo) {
                // If a friend turns off their Wi-Fi, instantly remove them from the list
                mainHandler.post {
                    activeNodes.removeAll { it.id == service.serviceName }
                }
            }

            override fun onDiscoveryStopped(serviceType: String) {}
            override fun onStartDiscoveryFailed(serviceType: String, errorCode: Int) {
                nsdManager?.stopServiceDiscovery(this)
            }
            override fun onStopDiscoveryFailed(serviceType: String, errorCode: Int) {
                nsdManager?.stopServiceDiscovery(this)
            }
        }

        nsdManager?.discoverServices(SERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD, discoveryListener)
    }

    fun stopDiscovery() {
        discoveryListener?.let { nsdManager?.stopServiceDiscovery(it) }
        discoveryListener = null
        mainHandler.post { activeNodes.clear() }
    }
}