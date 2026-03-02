package com.ltcoe.meshvault.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ltcoe.meshvault.ui.theme.AccentCyan
import com.ltcoe.meshvault.ui.theme.DarkBackground
import com.ltcoe.meshvault.ui.theme.SurfaceDark

@Composable
fun LoginScreen(onLoginClick: () -> Unit) {
    var nodeAddress by remember { mutableStateOf("0x7a...4f2B") }
    var masterKey by remember { mutableStateOf("••••••••••••") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(80.dp))

        // Hexagon Logo Placeholder (You can replace with an actual Image/Icon)
        Box(
            modifier = Modifier
                .size(64.dp)
                .border(3.dp, AccentCyan, RoundedCornerShape(16.dp))
                .shadow(15.dp, spotColor = AccentCyan) // Glow effect
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Welcome Back",
            color = Color.White,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Access your encrypted decentralized storage\nnodes.",
            color = Color.LightGray,
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(48.dp))

        // The Input Card
        Card(
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {

                Text("NODE ADDRESS", color = Color.LightGray, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = nodeAddress,
                    onValueChange = { nodeAddress = it },
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = DarkBackground,
                        focusedContainerColor = DarkBackground,
                        unfocusedBorderColor = Color.Transparent,
                        focusedBorderColor = AccentCyan,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text("MASTER KEY", color = Color.LightGray, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = masterKey,
                    onValueChange = { masterKey = it },
                    visualTransformation = PasswordVisualTransformation(),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = DarkBackground,
                        focusedContainerColor = DarkBackground,
                        unfocusedBorderColor = Color.Transparent,
                        focusedBorderColor = AccentCyan,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // The Cyan Login Button
                Button(
                    onClick = onLoginClick,
                    colors = ButtonDefaults.buttonColors(containerColor = AccentCyan),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) {
                    Text("Decrypt & Connect", color = DarkBackground, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Login", tint = DarkBackground)
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Biometric Bottom Button
        TextButton(onClick = { /* Trigger Biometrics */ }) {
            Text("Use Biometric Auth", color = Color.LightGray)
        }
        Spacer(modifier = Modifier.height(32.dp))
    }
}