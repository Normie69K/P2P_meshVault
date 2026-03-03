package com.ltcoe.meshvault.ui.screens.vault

import android.graphics.BitmapFactory
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.ltcoe.meshvault.ui.theme.*
import com.ltcoe.meshvault.util.ApiClient
import com.ltcoe.meshvault.util.DashboardFile
import com.ltcoe.meshvault.util.DownloadManager
import com.ltcoe.meshvault.util.SecureStorageManager
import kotlinx.coroutines.launch
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen() {
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()
    val secureStorage = remember { SecureStorageManager(context) }

    // UI State
    var isLoading by remember { mutableStateOf(true) }
    var realFiles by remember { mutableStateOf<List<DashboardFile>>(emptyList()) }
    var selectedFile by remember { mutableStateOf<DashboardFile?>(null) }
    var showSheet by remember { mutableStateOf(false) }
    var previewFile by remember { mutableStateOf<File?>(null) } // Holds the decrypted file for viewing

    LaunchedEffect(Unit) {
        realFiles = ApiClient.getMyFiles()
        isLoading = false
    }

    // --- 1. FULL SCREEN IMAGE PREVIEWER ---
    if (previewFile != null) {
        Dialog(onDismissRequest = { previewFile = null }) {
            Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
                val bitmap = remember(previewFile) { BitmapFactory.decodeFile(previewFile!!.absolutePath)?.asImageBitmap() }

                if (bitmap != null) {
                    Image(bitmap = bitmap, contentDescription = "Decrypted Image", modifier = Modifier.fillMaxSize())
                } else {
                    Text("Preview not available for this file type.", color = Color.White, modifier = Modifier.align(Alignment.Center))
                }

                IconButton(
                    onClick = { previewFile = null },
                    modifier = Modifier.align(Alignment.TopEnd).padding(16.dp).background(Color.DarkGray.copy(alpha = 0.5f), CircleShape)
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                }
            }
        }
    }

    // --- 2. ACTION BOTTOM SHEET (Preview vs Save) ---
    if (showSheet && selectedFile != null) {
        ModalBottomSheet(onDismissRequest = { showSheet = false }, sheetState = sheetState, containerColor = SurfaceDark) {
            Column(modifier = Modifier.padding(24.dp).fillMaxWidth().padding(bottom = 32.dp)) {
                Text(selectedFile!!.name, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text(selectedFile!!.details, color = Color.LightGray, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(32.dp))

                // OPTION A: PREVIEW IN APP
                Button(
                    onClick = {
                        scope.launch {
                            showSheet = false
                            Toast.makeText(context, "Decrypting for Preview...", Toast.LENGTH_SHORT).show()
                            val key = secureStorage.getFileKey(selectedFile!!.id)
                            if (key != null) {
                                val file = DownloadManager.downloadAndDecryptFile(context, selectedFile!!.id, selectedFile!!.name, key, isForPreview = true)
                                if (file != null) previewFile = file else Toast.makeText(context, "Decryption Failed", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AccentCyan, contentColor = DarkBackground),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Visibility, contentDescription = null)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Preview File", fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // OPTION B: EXPORT TO DEVICE
                OutlinedButton(
                    onClick = {
                        scope.launch {
                            showSheet = false
                            Toast.makeText(context, "Exporting to Downloads...", Toast.LENGTH_SHORT).show()
                            val key = secureStorage.getFileKey(selectedFile!!.id)
                            if (key != null) {
                                val file = DownloadManager.downloadAndDecryptFile(context, selectedFile!!.id, selectedFile!!.name, key, isForPreview = false)
                                if (file != null) Toast.makeText(context, "Saved to Device Downloads!", Toast.LENGTH_LONG).show()
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                    border = BorderStroke(1.dp, SurfaceDark),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Download, contentDescription = null, tint = AccentCyan)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Save to Device", fontWeight = FontWeight.Medium)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // --- NEW: OPTION C: DELETE FROM NETWORK ---
                Button(
                    onClick = {
                        scope.launch {
                            showSheet = false
                            Toast.makeText(context, "Sending kill signal to network...", Toast.LENGTH_SHORT).show()

                            val deleted = ApiClient.deleteFile(selectedFile!!.id)
                            Toast.makeText(context, "File completely wiped from Mesh!", Toast.LENGTH_LONG).show()

                            // Refresh the list
                            realFiles = ApiClient.getMyFiles()
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red.copy(alpha = 0.15f), contentColor = Color.Red),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color.Red.copy(alpha = 0.5f))
                ) {
                    Icon(Icons.Default.DeleteForever, contentDescription = "Delete")
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Delete from Vault", fontWeight = FontWeight.Bold)
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
        Column(modifier = Modifier.fillMaxSize().background(DarkBackground).padding(horizontal = 24.dp).verticalScroll(scrollState)) {
            Spacer(modifier = Modifier.height(48.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
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
            StorageCard()
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                StatCard(modifier = Modifier.weight(1f), icon = Icons.AutoMirrored.Filled.ShowChart, title = "ACTIVE NODES", value = "1", suffix = " / 1")
                StatCard(modifier = Modifier.weight(1f), icon = Icons.Default.Timer, title = "HEALTH", value = "100", suffix = " %")
            }
            Spacer(modifier = Modifier.height(32.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Recent Files", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Medium)
                Text("VIEW ALL", color = AccentCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(16.dp))

            if (realFiles.isEmpty()) {
                Text("Your vault is empty.", color = Color.LightGray, fontSize = 14.sp)
            } else {
                realFiles.forEach { file ->
                    FileItem(name = file.name, meta = file.details, onClick = { selectedFile = file; showSheet = true })
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