package com.ltcoe.meshvault.ui.screens.vault

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.InsertDriveFile
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ltcoe.meshvault.ui.theme.*
import com.ltcoe.meshvault.util.ApiClient
import com.ltcoe.meshvault.util.DashboardFile
import com.ltcoe.meshvault.util.DownloadManager
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen() {
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()

    // UI State
    var isLoading by remember { mutableStateOf(true) }
    var realFiles by remember { mutableStateOf<List<DashboardFile>>(emptyList()) }
    var selectedFile by remember { mutableStateOf<DashboardFile?>(null) }
    var showSheet by remember { mutableStateOf(false) }

    // Fetch the real list of files from Arch Linux on startup
    LaunchedEffect(Unit) {
        realFiles = ApiClient.getMyFiles()
        isLoading = false
    }

    // --- 1. ACTION BOTTOM SHEET ---
    if (showSheet && selectedFile != null) {
        ModalBottomSheet(
            onDismissRequest = { showSheet = false },
            sheetState = sheetState,
            containerColor = SurfaceDark
        ) {
            Column(modifier = Modifier.padding(24.dp).fillMaxWidth().padding(bottom = 32.dp)) {
                Text(selectedFile!!.name, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text(selectedFile!!.details, color = Color.LightGray, fontSize = 14.sp)

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        scope.launch {
                            showSheet = false
                            Toast.makeText(context, "Initializing secure download...", Toast.LENGTH_SHORT).show()

                            // 1. Trigger the real download & decrypt flow
                            // Note: You'll need to pass the real SecretKey from SecureStorage in the next step
                            val success = DownloadManager.downloadAndDecryptFile(
                                context = context,
                                fileId = selectedFile!!.id,
                                fileName = selectedFile!!.name,
                                fileKey = null
                            )

                            if (success) {
                                Toast.makeText(context, "File saved to Downloads", Toast.LENGTH_LONG).show()
                            } else {
                                Toast.makeText(context, "Decryption Failed", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AccentCyan, contentColor = DarkBackground),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Download, contentDescription = null)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Download & Decrypt", fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize().background(DarkBackground), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator(color = AccentCyan, strokeWidth = 3.dp)
                Spacer(modifier = Modifier.height(16.dp))
                Text("Syncing Vault...", color = AccentCyan, fontSize = 14.sp)
            }
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(DarkBackground)
                .padding(horizontal = 24.dp)
                .verticalScroll(scrollState)
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            // --- HEADER ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Dashboard", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Security, contentDescription = null, tint = AccentCyan, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("End-to-End Encrypted", color = AccentCyan, fontSize = 12.sp)
                    }
                }
                IconButton(onClick = { }, modifier = Modifier.background(SurfaceDark, CircleShape).size(40.dp)) {
                    Icon(Icons.Default.Notifications, contentDescription = null, tint = AccentCyan)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // --- STORAGE CARD ---
            StorageCard()

            Spacer(modifier = Modifier.height(16.dp))

            // --- STATS ROW ---
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                StatCard(modifier = Modifier.weight(1f), icon = Icons.AutoMirrored.Filled.ShowChart, title = "ACTIVE NODES", value = "1", suffix = " / 1")
                StatCard(modifier = Modifier.weight(1f), icon = Icons.Default.Timer, title = "HEALTH", value = "100", suffix = " %")
            }

            Spacer(modifier = Modifier.height(32.dp))

            // --- RECENT FILES LIST ---
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Recent Files", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Medium)
                Text("VIEW ALL", color = AccentCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (realFiles.isEmpty()) {
                Text("Your vault is empty.", color = Color.LightGray, fontSize = 14.sp)
            } else {
                realFiles.forEach { file ->
                    FileItem(
                        name = file.name,
                        meta = file.details,
                        onClick = {
                            selectedFile = file
                            showSheet = true
                        }
                    )
                }
            }
            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

@Composable
fun StorageCard() {
    Card(colors = CardDefaults.cardColors(containerColor = SurfaceDark), shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("VAULT STATUS", color = Color.LightGray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text("Arch Linux Node Connected", color = Color.White, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(24.dp))
            LinearProgressIndicator(progress = { 0.1f }, color = AccentCyan, trackColor = DarkBackground, modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)))
        }
    }
}

@Composable
fun StatCard(modifier: Modifier, icon: ImageVector, title: String, value: String, suffix: String) {
    Card(colors = CardDefaults.cardColors(containerColor = SurfaceDark), shape = RoundedCornerShape(12.dp), modifier = modifier) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(title, color = Color.LightGray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(value, color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Light)
                Text(suffix, color = AccentCyan, fontSize = 14.sp, modifier = Modifier.padding(bottom = 3.dp))
            }
        }
    }
}

@Composable
fun FileItem(name: String, meta: String, onClick: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
            .border(1.dp, SurfaceDark, RoundedCornerShape(8.dp))
            .clickable { onClick() }
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(40.dp).background(SurfaceDark, RoundedCornerShape(8.dp)), contentAlignment = Alignment.Center) {
                Icon(Icons.AutoMirrored.Filled.InsertDriveFile, contentDescription = null, tint = Color.LightGray)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(name, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                Text(meta, color = Color.LightGray, fontSize = 12.sp)
            }
        }
    }
}