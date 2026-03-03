package com.ltcoe.meshvault.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.ltcoe.meshvault.ui.components.CustomBottomNavBar
import com.ltcoe.meshvault.ui.navigation.Screen
import com.ltcoe.meshvault.ui.screens.auth.LoginScreen
import com.ltcoe.meshvault.ui.screens.vault.DashboardScreen
import com.ltcoe.meshvault.ui.screens.vault.NodesScreen
import com.ltcoe.meshvault.ui.screens.vault.SettingsScreen
import com.ltcoe.meshvault.ui.screens.vault.UploadScreen
import com.ltcoe.meshvault.ui.theme.DarkBackground
import com.ltcoe.meshvault.util.SecureStorageManager

@Composable
fun MainApp() {
    val navController = rememberNavController()
    val context = LocalContext.current

    // Check if the user already has a wallet
    val secureStorage = remember { SecureStorageManager(context) }
    val startRoute = if (secureStorage.hasWallet()) Screen.Dashboard.route else Screen.Login.route

    // Track the current route to hide/show bottom bar
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        containerColor = DarkBackground,
        bottomBar = {
            if (currentRoute != Screen.Login.route) {
                CustomBottomNavBar(
                    currentRoute = currentRoute ?: "",
                    onNavigate = { route ->
                        // This handles the actual navigation when a bottom tab is clicked
                        navController.navigate(route) {
                            // These standard flags prevent the app from opening 50 copies of the same screen
                            navController.graph.startDestinationRoute?.let { startRoute ->
                                popUpTo(startRoute) { saveState = true }
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        // Use the dynamic startRoute here instead of hardcoding Screen.Login.route
        NavHost(
            navController = navController,
            startDestination = startRoute,
            modifier = Modifier.padding(innerPadding)
        ) {
            // 1. Login Route
            composable(Screen.Login.route) { LoginScreen(navController = navController) }

            // 2. Dashboard Route
            composable(Screen.Dashboard.route) { DashboardScreen() }

            // 3. Files Route
            composable(Screen.Files.route) { PlaceholderScreen("All Files") }

            // 4. Upload Route
            composable(Screen.Upload.route) { UploadScreen() }

            // 5. Nodes Route
            composable(Screen.Nodes.route) { NodesScreen() }

            // 6. Settings Route
            composable(Screen.Settings.route) { SettingsScreen() }
        }
    }
}

// A temporary screen so we don't get crashes before we build the real ones
@Composable
fun PlaceholderScreen(title: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground),
        contentAlignment = Alignment.Center
    ) {
        Text(text = title, color = Color.White)
    }
}