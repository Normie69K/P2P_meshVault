package com.ltcoe.meshvault.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.ltcoe.meshvault.ui.navigation.Screen
import com.ltcoe.meshvault.ui.theme.AccentCyan
import com.ltcoe.meshvault.ui.theme.DarkBackground
import com.ltcoe.meshvault.ui.theme.SurfaceDark

@Composable
fun CustomBottomNavBar(
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    // The Box allows us to stack the floating button ON TOP of the navigation bar
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.BottomCenter
    ) {

        // The Dark Pill-Shaped Background
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(SurfaceDark, shape = RoundedCornerShape(32.dp))
                .border(1.dp, Color(0xFF2A303C), RoundedCornerShape(32.dp)) // Subtle border
                .padding(horizontal = 24.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left Side Icons
            NavIcon(Icons.Default.Home, Screen.Dashboard.route, currentRoute, onNavigate)
            NavIcon(Icons.Default.Folder, Screen.Files.route, currentRoute, onNavigate)

            // Empty space in the middle so the floating button has room
            Spacer(modifier = Modifier.width(48.dp))

            // Right Side Icons
            NavIcon(Icons.Default.Storage, Screen.Nodes.route, currentRoute, onNavigate)
            NavIcon(Icons.Default.Settings, Screen.Settings.route, currentRoute, onNavigate)
        }

        // The Cyan Floating Upload Button
        FloatingActionButton(
            onClick = { onNavigate(Screen.Upload.route) },
            containerColor = AccentCyan,
            contentColor = DarkBackground,
            shape = CircleShape,
            modifier = Modifier
                .offset(y = (-24).dp) // Pushes it up to overlap the bar
                .size(64.dp)
                .shadow(12.dp, spotColor = AccentCyan, shape = CircleShape) // Glow effect
        ) {
            Icon(
                imageVector = Icons.Default.CloudUpload,
                contentDescription = "Upload",
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

// Helper composable for the individual icons
@Composable
fun NavIcon(
    icon: ImageVector,
    route: String,
    currentRoute: String,
    onClick: (String) -> Unit
) {
    val isSelected = currentRoute == route

    IconButton(onClick = { onClick(route) }) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (isSelected) AccentCyan else Color.LightGray,
            modifier = Modifier.size(28.dp)
        )

        // Optional: Add the little cyan dot under the selected icon like in your design
        if (isSelected) {
            Box(
                modifier = Modifier
                    .offset(y = 20.dp)
                    .size(4.dp)
                    .background(AccentCyan, CircleShape)
            )
        }
    }
}