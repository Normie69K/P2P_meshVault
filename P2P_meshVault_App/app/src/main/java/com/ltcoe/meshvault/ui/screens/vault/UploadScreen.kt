package com.ltcoe.meshvault.ui.screens.vault

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ltcoe.meshvault.ui.theme.*
import com.ltcoe.meshvault.util.FileSharder
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch

@Composable
fun UploadScreen() {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var selectedFileUri by remember { mutableStateOf<Uri?>(null) }
    var selectedFileName by remember { mutableStateOf<String?>(null) }
    var chunkCount by remember { mutableStateOf(0) }
    var isProcessing by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedFileUri = uri
            selectedFileName = uri.path?.substringAfterLast('/')

            // Launch the background work!
            coroutineScope.launch {
                isProcessing = true // Show loading
                val shards = FileSharder.shardFile(context, uri)
                chunkCount = shards.size
                isProcessing = false // Hide loading
                println("SUCCESS: Sliced ${selectedFileName} into ${shards.size} separate 1MB chunks!")
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        // --- 1. HEADER ---
        Text("Secure Upload", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "Files are encrypted before leaving your device.",
            color = Color.LightGray,
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(32.dp))

        // --- 2. DASHED DROP ZONE ---
        // This is the custom dashed border matching your UI
        val dashEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 20f), 0f)
        val strokeColor = AccentCyan.copy(alpha = 0.3f) // Slightly faded cyan

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f) // Takes up the remaining space
                .padding(bottom = 120.dp) // Leave room for the custom bottom bar
                .drawBehind {
                    drawRoundRect(
                        color = strokeColor,
                        style = Stroke(width = 4f, pathEffect = dashEffect),
                        cornerRadius = CornerRadius(32f, 32f) // Matches 12.dp rounded corners
                    )
                }
                .clip(RoundedCornerShape(12.dp))
                .clickable { /* TODO: Launch Android File Picker Intent */ },
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Outer Glow/Circle for the Cloud Icon
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .background(SurfaceDark, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.CloudUpload,
                        contentDescription = "Upload",
                        tint = AccentCyan,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text("Tap to select file", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)

                Spacer(modifier = Modifier.height(8.dp))

                Text("or drag and drop here", color = Color.LightGray, fontSize = 14.sp)

                Spacer(modifier = Modifier.height(32.dp))

                // AES-256 Encryption Badge
                Row(
                    modifier = Modifier
                        .border(1.dp, SurfaceDark, RoundedCornerShape(20.dp))
                        .background(DarkBackground.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Security, contentDescription = "Security", tint = AccentCyan, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("AES-256 Encryption", color = Color.LightGray, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}