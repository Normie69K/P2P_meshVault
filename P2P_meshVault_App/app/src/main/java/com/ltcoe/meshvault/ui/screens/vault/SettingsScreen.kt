package com.ltcoe.meshvault.ui.screens.vault

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ltcoe.meshvault.ui.theme.*

@Composable
fun SettingsScreen(onLogoutClick:() -> Unit) {
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
        Text("Settings", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(32.dp))

        // --- 2. PROFILE CARD ---
        Card(
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Circular Avatar with Cyan Border
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .border(2.dp, AccentCyan, CircleShape)
                        .padding(4.dp)
                        .background(DarkBackground, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Person, contentDescription = "Profile", tint = Color.White, modifier = Modifier.size(28.dp))
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text("0x7a...4f2B", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Pro Tier", color = AccentCyan, fontSize = 14.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // --- 3. SECURITY SECTION ---
        Text("SECURITY", color = Color.LightGray, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Card(
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                SettingsSwitchRow(icon = Icons.Default.Fingerprint, title = "Biometric Unlock", initialState = true)
                HorizontalDivider(color = DarkBackground, thickness = 1.dp, modifier = Modifier.padding(horizontal = 16.dp))
                SettingsActionRow(icon = Icons.Default.Security, title = "Export Master Key")
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // --- 4. PREFERENCES SECTION ---
        Text("PREFERENCES", color = Color.LightGray, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Card(
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                SettingsSwitchRow(icon = Icons.Default.Notifications, title = "Push Notifications", initialState = false)
                HorizontalDivider(color = DarkBackground, thickness = 1.dp, modifier = Modifier.padding(horizontal = 16.dp))
                SettingsSwitchRow(icon = Icons.Default.Sync, title = "Background Sync", initialState = true)
            }
        }

        Spacer(modifier = Modifier.height(48.dp))

        // --- 5. DISCONNECT BUTTON ---
        OutlinedButton(
            onClick = onLogoutClick,
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
            border = BorderStroke(1.dp, Color(0x33FF5252)), // Faded red border matching the design
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Disconnect", tint = Color.Red)
            Spacer(modifier = Modifier.width(12.dp))
            Text("Disconnect Node", color = Color.Red, fontSize = 16.sp, fontWeight = FontWeight.Medium)
        }

        // Extra padding so the bottom nav bar doesn't cover the last button
        Spacer(modifier = Modifier.height(100.dp))
    }
}

// Helper Composable for rows with a Switch
@Composable
fun SettingsSwitchRow(icon: ImageVector, title: String, initialState: Boolean) {
    var isChecked by remember { mutableStateOf(initialState) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = title, tint = Color.LightGray, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Text(title, color = Color.White, fontSize = 16.sp)
        }
        Switch(
            checked = isChecked,
            onCheckedChange = { isChecked = it },
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = AccentCyan,
                uncheckedThumbColor = Color.LightGray,
                uncheckedTrackColor = DarkBackground,
                uncheckedBorderColor = Color.Transparent
            )
        )
    }
}

// Helper Composable for clickable rows with an Arrow
@Composable
fun SettingsActionRow(icon: ImageVector, title: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = title, tint = Color.LightGray, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Text(title, color = Color.White, fontSize = 16.sp)
        }
        Icon(Icons.Default.ChevronRight, contentDescription = "Go", tint = Color.LightGray)
    }
}