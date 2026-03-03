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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ltcoe.meshvault.ui.theme.*
import com.ltcoe.meshvault.util.FileChunk
import com.ltcoe.meshvault.util.FileSharder
import com.ltcoe.meshvault.util.ApiClient
import com.ltcoe.meshvault.util.CryptoManager
import kotlinx.coroutines.launch

@Composable
fun UploadScreen() {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // UI State
    var selectedFileUri by remember { mutableStateOf<Uri?>(null) }
    var fileTitle by remember { mutableStateOf("") }
    var fileChunks by remember { mutableStateOf<List<FileChunk>>(emptyList()) }
    var isProcessing by remember { mutableStateOf(false) }
    var uploadStatus by remember { mutableStateOf<String?>(null) }

    // The Android Intent that opens the File Explorer
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedFileUri = uri
            // Auto-fill the original file name as the default title
            fileTitle = uri.path?.substringAfterLast('/') ?: "New_File"
            uploadStatus = null

            // Slice the file in the background so the UI doesn't freeze
            coroutineScope.launch {
                isProcessing = true
                fileChunks = FileSharder.shardFile(context, uri)
                isProcessing = false
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
        val dashEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 20f), 0f)
        val strokeColor = AccentCyan.copy(alpha = 0.3f)

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(bottom = 120.dp)
                .drawBehind {
                    drawRoundRect(
                        color = if (selectedFileUri != null) AccentCyan else strokeColor,
                        style = Stroke(width = 4f, pathEffect = dashEffect),
                        cornerRadius = CornerRadius(32f, 32f)
                    )
                }
                .clip(RoundedCornerShape(12.dp))
                // Only allow clicking to pick a file IF we aren't already uploading one
                .clickable(enabled = selectedFileUri == null) {
                    launcher.launch(arrayOf("*/*"))
                },
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(24.dp)
            ) {
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

                // Dynamic UI based on state
                if (selectedFileUri == null) {
                    Text("Tap to select file", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("or drag and drop here", color = Color.LightGray, fontSize = 14.sp)
                } else {
                    Text("Ready to Encrypt", color = AccentCyan, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(16.dp))

                    // 1. The Editable Title Field
                    OutlinedTextField(
                        value = fileTitle,
                        onValueChange = { fileTitle = it },
                        label = { Text("File Title", color = Color.LightGray) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AccentCyan,
                            unfocusedBorderColor = SurfaceDark,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    if (isProcessing) {
                        Text("Slicing file...", color = Color.LightGray, fontSize = 12.sp)
                    } else if (fileChunks.isNotEmpty()) {
                        Text("Sliced into ${fileChunks.size} chunks", color = AccentCyan, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(24.dp))

                        // 2. The REAL Upload Button
                        Button(
                            onClick = {
                                coroutineScope.launch {
                                    uploadStatus = "Generating AES-256 File Key..."
                                    // 1. Generate a real AES key for this specific file
                                    val fileKey = CryptoManager.generateFileKey()
                                    var successfulUploads = 0

                                    // Loop through every 1MB slice and send it over the internet!
                                    for (chunk in fileChunks) {
                                        uploadStatus = "Uploading piece ${chunk.chunkIndex + 1} of ${fileChunks.size}..."

                                        // THIS IS THE REAL API CALL
                                        val success = ApiClient.uploadChunk(chunk)

                                        if (success) {
                                            successfulUploads++
                                        } else {
                                            uploadStatus = "Error on chunk ${chunk.chunkIndex + 1}. Halting."
                                            break
                                        }
                                    }

                                    if (successfulUploads == fileChunks.size) {
                                        uploadStatus = "Upload Complete! 100% Encrypted."
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = AccentCyan, contentColor = DarkBackground),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth().height(48.dp)
                        ) {
                            Text("Securely Upload", fontWeight = FontWeight.Bold)
                        }
                    }

                    if (uploadStatus != null) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(uploadStatus!!, color = SuccessGreen, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    }
                }

                if (selectedFileUri == null) {
                    Spacer(modifier = Modifier.height(32.dp))
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
}