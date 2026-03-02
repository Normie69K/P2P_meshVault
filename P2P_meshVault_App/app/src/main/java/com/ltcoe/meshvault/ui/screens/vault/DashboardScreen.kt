package com.ltcoe.meshvault.ui.screens.vault

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ltcoe.meshvault.ui.theme.*

@Composable
fun DashboardScreen() {
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
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Dashboard", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Security, contentDescription = "Secure", tint = AccentCyan, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("End-to-End Encrypted", color = AccentCyan, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                }
            }
            IconButton(
                onClick = { /* TODO */ },
                modifier = Modifier
                    .background(SurfaceDark, CircleShape)
                    .size(40.dp)
            ) {
                Icon(Icons.Default.Notifications, contentDescription = "Alerts", tint = AccentCyan, modifier = Modifier.size(20.dp))
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // --- 2. STORAGE CARD ---
        Card(
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column {
                        Text("TOTAL STORAGE", color = Color.LightGray, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text("45.2", color = Color.White, fontSize = 36.sp, fontWeight = FontWeight.Light)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("GB", color = Color.LightGray, fontSize = 16.sp, modifier = Modifier.padding(bottom = 6.dp))
                        }
                    }
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(DarkBackground, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Cloud, contentDescription = null, tint = AccentCyan)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Progress Bar
                LinearProgressIndicator(
                    progress = 0.45f,
                    color = AccentCyan,
                    trackColor = DarkBackground,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("45% Used", color = Color.White, fontSize = 12.sp)
                    Text("100 GB Total", color = Color.LightGray, fontSize = 12.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- 3. STATS ROW ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            StatCard(modifier = Modifier.weight(1f), icon = Icons.Default.ShowChart, title = "ACTIVE NODES", value = "12", suffix = " / 15")
            StatCard(modifier = Modifier.weight(1f), icon = Icons.Default.Timer, title = "EXPIRING", value = "3", suffix = " files")
        }

        Spacer(modifier = Modifier.height(32.dp))

        // --- 4. RECENT FILES LIST ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Recent Files", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Medium)
            Text("VIEW ALL", color = AccentCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))

        FileItem("Project_Alpha_Specs.pdf", "2.4 MB • 2h ago")
        FileItem("Design_Assets.zip", "145 MB • 5h ago")
        FileItem("Q3_Financials.xlsx", "1.1 MB • 1d ago")

        // Extra space at the bottom so the floating button doesn't hide the last file
        Spacer(modifier = Modifier.height(100.dp))
    }
}

// Helper Composable for the small stat cards
@Composable
fun StatCard(modifier: Modifier, icon: ImageVector, title: String, value: String, suffix: String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(title, color = Color.LightGray, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(value, color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Light)
                Text(suffix, color = AccentCyan, fontSize = 14.sp, modifier = Modifier.padding(bottom = 3.dp))
            }
        }
    }
}

// Helper Composable for the recent files
@Composable
fun FileItem(name: String, meta: String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
            .border(1.dp, SurfaceDark, RoundedCornerShape(8.dp))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(SurfaceDark, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.InsertDriveFile, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(name, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(4.dp))
                Text(meta, color = Color.LightGray, fontSize = 12.sp)
            }
        }
    }
}