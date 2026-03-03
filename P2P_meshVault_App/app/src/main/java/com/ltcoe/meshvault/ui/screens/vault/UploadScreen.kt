package com.ltcoe.meshvault.ui.screens.vault

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Tag
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ltcoe.meshvault.ui.theme.*
import com.ltcoe.meshvault.util.*
import kotlinx.coroutines.launch

@Composable
fun UploadScreen() {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val secureStorage = remember { SecureStorageManager(context) }

    // UI State
    var selectedFileUri by remember { mutableStateOf<Uri?>(null) }
    var fileTitle by remember { mutableStateOf("") }
    var fileTag by remember { mutableStateOf("") }
    var filePassword by remember { mutableStateOf("") }
    var rentDuration by remember { mutableStateOf("Unlimited") }
    val durationOptions = listOf("1 Hour", "24 Hours", "7 Days", "Unlimited")
    var fileChunks by remember { mutableStateOf<List<FileChunk>>(emptyList()) }

    // Upload Progress State
    var isUploading by remember { mutableStateOf(false) }
    var uploadProgress by remember { mutableFloatStateOf(0f) }
    val uploadLogs = remember { mutableStateListOf<String>() }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
        if (uri != null) {
            selectedFileUri = uri
            fileTitle = uri.path?.substringAfterLast('/') ?: "New_File"
            uploadLogs.clear()
            uploadProgress = 0f

            coroutineScope.launch {
                uploadLogs.add(">> Analyzing file structure...")
                fileChunks = FileSharder.shardFile(context, uri)
                uploadLogs.add(">> File successfully split into ${fileChunks.size} shards.")
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize().background(DarkBackground).padding(horizontal = 24.dp)) {
        Spacer(modifier = Modifier.height(48.dp))
        Text("Secure Upload", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)

        // --- TOP NOTIFICATION BAR ---
        if (isUploading) {
            Spacer(modifier = Modifier.height(16.dp))
            LinearProgressIndicator(
                progress = { uploadProgress },
                modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                color = AccentCyan, trackColor = SurfaceDark
            )
            Text("${(uploadProgress * 100).toInt()}% Complete", color = AccentCyan, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
        }

        Spacer(modifier = Modifier.height(32.dp))

        if (selectedFileUri == null) {
            // --- DASHED DROP ZONE ---
            val dashEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 20f), 0f)
            val strokeColor = AccentCyan.copy(alpha = 0.3f)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(bottom = 120.dp)
                    .drawBehind {
                        drawRoundRect(
                            color = strokeColor,
                            style = Stroke(width = 4f, pathEffect = dashEffect),
                            cornerRadius = CornerRadius(32f, 32f)
                        )
                    }
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { launcher.launch(arrayOf("*/*")) },
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(24.dp)) {
                    Box(modifier = Modifier.size(72.dp).background(SurfaceDark, CircleShape), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.CloudUpload, contentDescription = "Upload", tint = AccentCyan, modifier = Modifier.size(32.dp))
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    Text("Tap to select file", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("or drag and drop here", color = Color.LightGray, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(32.dp))
                    Row(
                        modifier = Modifier.border(1.dp, SurfaceDark, RoundedCornerShape(20.dp)).background(DarkBackground.copy(alpha = 0.5f), RoundedCornerShape(20.dp)).padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Security, contentDescription = "Security", tint = AccentCyan, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("AES-256 Encryption", color = Color.LightGray, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }
        } else {
            // --- METADATA FORM ---
            Text("Ready to Encrypt", color = AccentCyan, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = fileTitle, onValueChange = { fileTitle = it },
                label = { Text("File Title", color = Color.LightGray) },
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AccentCyan, unfocusedBorderColor = SurfaceDark, focusedTextColor = Color.White),
                singleLine = true, modifier = Modifier.fillMaxWidth(), enabled = !isUploading
            )
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = fileTag, onValueChange = { fileTag = it },
                label = { Text("Tag (e.g. Work, Personal)", color = Color.LightGray) },
                leadingIcon = { Icon(Icons.Default.Tag, contentDescription = null, tint = Color.LightGray) },
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AccentCyan, unfocusedBorderColor = SurfaceDark, focusedTextColor = Color.White),
                singleLine = true, modifier = Modifier.fillMaxWidth(), enabled = !isUploading
            )
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = filePassword, onValueChange = { filePassword = it },
                label = { Text("Extra Password (Optional)", color = Color.LightGray) },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = Color.LightGray) },
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AccentCyan, unfocusedBorderColor = SurfaceDark, focusedTextColor = Color.White),
                singleLine = true, modifier = Modifier.fillMaxWidth(), enabled = !isUploading
            )

            Spacer(modifier = Modifier.height(16.dp))
            Text("Storage Rental Duration", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(8.dp))

            // --- RENT TIMER SELECTION ---
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                durationOptions.forEach { option ->
                    val isSelected = rentDuration == option
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) AccentCyan else SurfaceDark)
                            .clickable { rentDuration = option }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = option,
                            color = if (isSelected) DarkBackground else Color.LightGray,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- TERMINAL LOGS ---
            Box(modifier = Modifier.fillMaxWidth().height(150.dp).background(Color.Black, RoundedCornerShape(8.dp)).padding(12.dp)) {
                LazyColumn(modifier = Modifier.fillMaxSize(), reverseLayout = true) {
                    items(uploadLogs.reversed()) { log ->
                        Text(log, color = SuccessGreen, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (!isUploading && fileChunks.isNotEmpty()) {
                Button(
                    onClick = {
                        coroutineScope.launch {
                            isUploading = true
                            uploadLogs.add(">> Generating AES-256 Master Key...")

                            val fileKey = CryptoManager.generateFileKey()
                            var successfulUploads = 0

                            for (chunk in fileChunks) {
                                uploadLogs.add(">> Encrypting Shard ${chunk.chunkIndex + 1}/${fileChunks.size}...")
                                val encryptedData = CryptoManager.encryptChunk(chunk.data, fileKey)
                                val secureChunk = chunk.copy(data = encryptedData)

                                uploadLogs.add(">> Pushing Shard ${chunk.chunkIndex + 1} to Node...")
                                // Adding the tag to the title temporarily so the backend can see it without needing Ktor updates yet
                                val finalTitle = "[$fileTag] [$rentDuration] $fileTitle"
                                val success = ApiClient.uploadChunk(secureChunk, finalTitle)

                                if (success) {
                                    successfulUploads++
                                    uploadProgress = successfulUploads.toFloat() / fileChunks.size
                                } else {
                                    uploadLogs.add("[!] ERROR: Network timeout on Shard ${chunk.chunkIndex + 1}")
                                    break
                                }
                            }

                            if (successfulUploads == fileChunks.size) {
                                uploadLogs.add(">> Securing Master Key to Hardware...")
                                secureStorage.saveFileKey(fileChunks[0].fileId, fileKey)
                                uploadLogs.add(">> UPLOAD COMPLETE & SECURED.")
                            }
                            isUploading = false
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AccentCyan, contentColor = DarkBackground),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Commence Secure Upload", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}