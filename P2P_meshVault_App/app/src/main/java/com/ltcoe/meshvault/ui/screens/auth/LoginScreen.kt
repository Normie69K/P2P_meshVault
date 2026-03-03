package com.ltcoe.meshvault.ui.screens.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AddModerator
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.ltcoe.meshvault.ui.navigation.Screen
import com.ltcoe.meshvault.ui.theme.*
import com.ltcoe.meshvault.util.CryptoManager
import com.ltcoe.meshvault.util.SecureStorageManager

enum class AuthStep { WELCOME, CREATE, IMPORT }

@Composable
fun LoginScreen(navController: NavController) {
    var currentStep by remember { mutableStateOf(AuthStep.WELCOME) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(80.dp))

        // App Logo & Title
        Icon(Icons.Default.AddModerator, contentDescription = "Logo", tint = AccentCyan, modifier = Modifier.size(64.dp))
        Spacer(modifier = Modifier.height(16.dp))
        Text("MeshVault", color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
        Text("Decentralized Storage Node", color = Color.LightGray, fontSize = 14.sp)

        Spacer(modifier = Modifier.height(64.dp))

        // Switch between the different UI states
        when (currentStep) {
            AuthStep.WELCOME -> WelcomeOptions(
                onCreateClick = { currentStep = AuthStep.CREATE },
                onImportClick = { currentStep = AuthStep.IMPORT }
            )
            AuthStep.CREATE -> CreateWalletFlow(navController)
            AuthStep.IMPORT -> ImportWalletFlow(navController)
        }
    }
}

@Composable
fun WelcomeOptions(onCreateClick: () -> Unit, onImportClick: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        // Option A: Create New
        Button(
            onClick = onCreateClick,
            colors = ButtonDefaults.buttonColors(containerColor = AccentCyan, contentColor = DarkBackground),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            Icon(Icons.Default.AddModerator, contentDescription = "Create")
            Spacer(modifier = Modifier.width(12.dp))
            Text("Create New Vault", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Option B: Import Existing
        OutlinedButton(
            onClick = onImportClick,
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
            border = BorderStroke(1.dp, SurfaceDark),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            Icon(Icons.Default.Key, contentDescription = "Import", tint = Color.LightGray)
            Spacer(modifier = Modifier.width(12.dp))
            Text("Import Master Key", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Medium)
        }
    }
}


@Composable
fun CreateWalletFlow(navController: NavController) {
    // 1. Generate the wallet ONLY ONCE when this screen opens
    val context = LocalContext.current
    val secureStorage = remember { SecureStorageManager(context) }
    val generatedWallet = remember { CryptoManager.createNewWallet() }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Your Master Key", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "Write this down offline. If you lose this key, your files are gone forever. MeshVault cannot recover it.",
            color = Color.Red,
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 2. Display the REAL generated private key hex
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(SurfaceDark, RoundedCornerShape(8.dp))
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            // We split the hex string with spaces every 8 characters to make it easier to read
            val formattedKey = generatedWallet.privateKeyHex.chunked(8).joinToString(" ")

            Text(
                text = formattedKey,
                color = AccentCyan,
                fontSize = 16.sp,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.sp,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Display the Public Address just so the user knows it
        Text("Node Address: 0x${generatedWallet.publicKeyHex.take(8)}...", color = Color.LightGray, fontSize = 12.sp)

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                secureStorage.saveWallet(
                    privateKeyHex = generatedWallet.privateKeyHex,
                    publicKeyHex = generatedWallet.publicKeyHex
                )

                navController.navigate(Screen.Dashboard.route) {
                    popUpTo(Screen.Login.route) { inclusive = true }
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = AccentCyan, contentColor = DarkBackground),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            Text("I Saved It Safely", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(8.dp))
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Go")
        }
    }
}

@Composable
fun ImportWalletFlow(navController: NavController) {
    var masterKey by remember { mutableStateOf("") }

    Column {
        OutlinedTextField(
            value = masterKey,
            onValueChange = { masterKey = it },
            label = { Text("Enter 64-character Hex Key", color = Color.LightGray) },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = AccentCyan,
                unfocusedBorderColor = SurfaceDark,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                cursorColor = AccentCyan
            ),
            singleLine = true,
            leadingIcon = { Icon(Icons.Default.VpnKey, contentDescription = "Key", tint = Color.LightGray) }
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                // TODO: Here we will verify the math before navigating
                navController.navigate(Screen.Dashboard.route) {
                    popUpTo(Screen.Login.route) { inclusive = true }
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = AccentCyan, contentColor = DarkBackground),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().height(56.dp),
            enabled = masterKey.isNotBlank()
        ) {
            Text("Restore Vault", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}