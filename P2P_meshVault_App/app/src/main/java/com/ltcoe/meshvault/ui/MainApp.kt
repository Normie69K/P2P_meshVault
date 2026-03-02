package com.ltcoe.meshvault.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.ltcoe.meshvault.ui.components.CustomBottomNavBar
import com.ltcoe.meshvault.ui.navigation.Screen
import com.ltcoe.meshvault.ui.screens.auth.LoginScreen
import com.ltcoe.meshvault.ui.screens.vault.DashboardScreen
import com.ltcoe.meshvault.ui.screens.vault.NodesScreen
import com.ltcoe.meshvault.ui.theme.DarkBackground

@Composable
@Preview
fun MainApp() {
    // The "Remote Control" for switching screens
    val navController = rememberNavController()

    // Observes which screen we are currently on
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: Screen.Login.route

    Scaffold(
        containerColor = DarkBackground,
        bottomBar = {
            // Only show the bottom bar if we are NOT on the Login screen
            if (currentRoute != Screen.Login.route) {
                CustomBottomNavBar(
                    currentRoute = currentRoute,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            // Pop up to the start destination of the graph to
                            // avoid building up a large stack of destinations
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            // Avoid multiple copies of the same destination
                            launchSingleTop = true
                            // Restore state when reselecting a previously selected item
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->

        // The "TV Screen" where the actual UI is displayed
        NavHost(
            navController = navController,
            startDestination = Screen.Login.route,
            modifier = Modifier.padding(innerPadding)
        ) {

            // 1. Login Route
            composable(Screen.Login.route) {
                LoginScreen(onLoginClick = {
                    // Navigate to Dashboard and destroy the Login screen so
                    // the user can't press the Android "Back" button to go back to login
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                })
            }

            // 2. Dashboard Route
            composable(Screen.Dashboard.route) { DashboardScreen() }

            // 3. Files Route
            composable(Screen.Files.route) { PlaceholderScreen("All Files") }

            // 4. Upload Route
            composable(Screen.Upload.route) { PlaceholderScreen("Secure Upload") }

            // 5. Nodes Route
            composable(Screen.Nodes.route) { NodesScreen() }

            // 6. Settings Route
            composable(Screen.Settings.route) { PlaceholderScreen("Settings") }
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