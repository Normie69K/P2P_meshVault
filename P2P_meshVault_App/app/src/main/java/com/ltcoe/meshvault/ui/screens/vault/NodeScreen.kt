package com.ltcoe.meshvault.ui.screens.vault

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ltcoe.meshvault.ui.theme.AccentCyan
import com.ltcoe.meshvault.ui.theme.DarkBackground
import com.ltcoe.meshvault.ui.theme.SuccessGreen
import com.ltcoe.meshvault.ui.theme.SurfaceDark
import com.ltcoe.meshvault.util.MeshNetworkManager
import com.ltcoe.meshvault.util.MeshNodeServer
import kotlinx.coroutines.launch

@Composable
fun NodesScreen() {
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var isNodeActive by remember { mutableStateOf(MeshNodeServer.isRunning) }
    var localIp by remember { mutableStateOf("Fetching IP...") }

    LaunchedEffect(Unit) {
        localIp = MeshNodeServer.getLocalIpAddress()
    }

    Column(modifier = Modifier.fillMaxSize().background(DarkBackground).padding(horizontal = 24.dp).verticalScroll(scrollState)) {
        Spacer(modifier = Modifier.height(48.dp))

        Text("Network Nodes", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Host or connect to the distributed mesh.", color = Color.LightGray, fontSize = 14.sp)

        Spacer(modifier = Modifier.height(32.dp))

        // --- HOST YOUR OWN NODE CARD ---
        Card(
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().border(1.dp, if (isNodeActive) SuccessGreen else SurfaceDark, RoundedCornerShape(12.dp))
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column {
                        Text("LOCAL NODE STATUS", color = Color.LightGray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(if (isNodeActive) "Broadcasting" else "Offline", color = if (isNodeActive) SuccessGreen else Color.White, fontSize = 20.sp, fontWeight = FontWeight.Medium)
                    }

                    IconButton(
                        onClick = {
                            // Wrapped the Heavy Server Boot Sequence in a background coroutine
                            scope.launch {
                                if (isNodeActive) {
                                    MeshNodeServer.stopServer()
                                    isNodeActive = false
                                } else {
                                    MeshNodeServer.startServer(context)
                                    isNodeActive = true
                                }
                            }
                        },
                        modifier = Modifier.background(if (isNodeActive) SuccessGreen.copy(alpha = 0.2f) else DarkBackground, RoundedCornerShape(8.dp))
                    ) {
                        Icon(Icons.Default.PowerSettingsNew, contentDescription = "Power", tint = if (isNodeActive) SuccessGreen else Color.LightGray)
                    }
                }

                if (isNodeActive) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Connect via Wi-Fi:", color = Color.LightGray, fontSize = 12.sp)
                    Text("http://$localIp:8080", color = AccentCyan, fontSize = 16.sp, fontFamily = FontFamily.Monospace)
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // --- TIER 1: GLOBAL NETWORK (Internet) ---
        Text("GLOBAL NETWORK", color = Color.LightGray, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
        Spacer(modifier = Modifier.height(16.dp))

        // This represents your permanent Arch Linux server!
        NodeItem(
            id = "Arch Linux Master",
            location = "archlinux.my-mesh.net",
            status = "ONLINE"
        )

        Spacer(modifier = Modifier.height(24.dp))

        // --- TIER 2: LOCAL PEERS (Wi-Fi Radar) ---
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("LOCAL PEERS", color = Color.LightGray, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            if (isNodeActive) {
                // A cool little scanning animation indicator
                Text("Scanning...", color = AccentCyan, fontSize = 10.sp)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        if (!isNodeActive) {
            Text("Turn on your Local Node to discover peers nearby.", color = Color.DarkGray, fontSize = 14.sp)
        } else if (MeshNetworkManager.activeNodes.isEmpty()) {
            LinearProgressIndicator(color = AccentCyan, trackColor = DarkBackground, modifier = Modifier.fillMaxWidth())
        } else {
            // Live phones discovered by your radar!
            MeshNetworkManager.activeNodes.forEach { peer ->
                NodeItem(
                    id = peer.id,
                    location = "IP: ${peer.ip}",
                    status = "ONLINE"
                )
            }
        }

        Spacer(modifier = Modifier.height(100.dp))
    }
}

// Updated Helper Composable to take real data
@Composable
fun NodeItem(id: String, location: String, status: String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
            .border(1.dp, SurfaceDark, RoundedCornerShape(8.dp))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                Row {
                    Box(modifier = Modifier.size(40.dp).background(SurfaceDark, RoundedCornerShape(8.dp)), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Dns, contentDescription = "Node", tint = Color.LightGray, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(id, color = Color.White, fontSize = 16.sp, fontFamily = FontFamily.Monospace)
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Public, contentDescription = "Location", tint = Color.LightGray, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(location, color = Color.LightGray, fontSize = 12.sp) // Renders the exact IP
                        }
                    }
                }
                Column(horizontalAlignment = Alignment.End) {
                    Box(modifier = Modifier.border(1.dp, SuccessGreen, RoundedCornerShape(4.dp)).padding(horizontal = 6.dp, vertical = 2.dp)) {
                        Text(status, color = SuccessGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}