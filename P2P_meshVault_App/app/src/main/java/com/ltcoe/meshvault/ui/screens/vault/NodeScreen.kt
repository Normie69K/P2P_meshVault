package com.ltcoe.meshvault.ui.screens.vault

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ltcoe.meshvault.ui.theme.*

@Composable
fun NodesScreen() {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(horizontal = 24.dp)
            .verticalScroll(scrollState)
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        // --- 1. HEADER ---
        Text("Network Nodes", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "Your files are sharded across these distributed\npeers.",
            color = Color.LightGray,
            fontSize = 14.sp,
            lineHeight = 20.sp
        )

        Spacer(modifier = Modifier.height(32.dp))

        // --- 2. NETWORK STATUS CARD ---
        Card(
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("NETWORK STATUS", color = Color.LightGray, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Healthy", color = SuccessGreen, fontSize = 20.sp, fontWeight = FontWeight.Medium)
                }
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(DarkBackground, RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Timeline, contentDescription = "Activity", tint = Color.LightGray)
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // --- 3. ACTIVE CONNECTIONS LIST ---
        Text("ACTIVE CONNECTIONS", color = Color.LightGray, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
        Spacer(modifier = Modifier.height(16.dp))

        // Here we render the list of individual nodes
        NodeItem(id = "nd-0x71...8a9F", location = "Frankfurt, EU", latency = "24ms", load = 0.45f, loadText = "45%")
        NodeItem(id = "nd-0x3b...1c2E", location = "Singapore, AS", latency = "32ms", load = 0.78f, loadText = "78%")
        NodeItem(id = "nd-0x9f...4d5B", location = "New York, US", latency = "115ms", load = 0.32f, loadText = "32%")
        NodeItem(id = "nd-0x2a...7f1C", location = "Tokyo, AS", latency = "142ms", load = 0.61f, loadText = "61%")

        Spacer(modifier = Modifier.height(100.dp)) // Extra padding so the bottom nav bar doesn't cover the last item
    }
}

// Helper Composable for each Node row
@Composable
fun NodeItem(id: String, location: String, latency: String, load: Float, loadText: String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
            .border(1.dp, SurfaceDark, RoundedCornerShape(8.dp))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Top Half: Identity and Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(SurfaceDark, RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Dns, contentDescription = "Node", tint = Color.LightGray, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(id, color = Color.White, fontSize = 16.sp, fontFamily = FontFamily.Monospace)
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Public, contentDescription = "Location", tint = Color.LightGray, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(location, color = Color.LightGray, fontSize = 12.sp)
                        }
                    }
                }

                // Right Side: Online Badge and Latency
                Column(horizontalAlignment = Alignment.End) {
                    Box(
                        modifier = Modifier
                            .border(1.dp, SuccessGreen, RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text("ONLINE", color = SuccessGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(latency, color = Color.LightGray, fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Bottom Half: Load Progress Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Load", color = Color.LightGray, fontSize = 12.sp, modifier = Modifier.width(36.dp))
                Spacer(modifier = Modifier.width(8.dp))
                LinearProgressIndicator(
                    progress = load,
                    color = AccentCyan,
                    trackColor = DarkBackground,
                    modifier = Modifier
                        .weight(1f)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(loadText, color = Color.LightGray, fontSize = 12.sp)
            }
        }
    }
}