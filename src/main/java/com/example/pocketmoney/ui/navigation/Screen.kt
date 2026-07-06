package com.example.pocketmoney.ui.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Transactions : Screen("transactions")
    object Settings : Screen("settings")
}