package com.ltcoe.meshvault.ui.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Dashboard : Screen("dashboard")
    object Files : Screen("files")
    object Upload : Screen("upload")
    object Nodes : Screen("nodes")
    object Settings : Screen("settings")
}