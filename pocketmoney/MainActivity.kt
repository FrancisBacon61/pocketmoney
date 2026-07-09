package com.example.pocketmoney

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.pocketmoney.ui.home.HomeScreen
import com.example.pocketmoney.ui.theme.PocketMoneyTheme
// Импорт твоих маршрутов (проверь путь, если создал файл в другом месте)
import com.example.pocketmoney.ui.navigation.Screen
import com.example.pocketmoney.ui.settings.SettingsScreen
import com.example.pocketmoney.ui.transactions.TransactionsScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Включаем отображение "от края до края" (под статус-баром)
        enableEdgeToEdge()

        setContent {
            PocketMoneyTheme {
                val navController = rememberNavController()

                // Базовый контейнер для навигации
                NavHost(
                    navController = navController,
                    startDestination = Screen.Home.route
                ) {
                    // Главный экран
                    composable(Screen.Home.route) {
                        HomeScreen(
                            onNavigateToTransactions = { navController.navigate(Screen.Transactions.route) },
                            onNavigateToSettings = { navController.navigate(Screen.Settings.route) }
                        )
                    }

                    composable(Screen.Transactions.route) {
                        TransactionsScreen(onBack = { navController.popBackStack() })
                    }

                    composable(Screen.Settings.route) {
                        SettingsScreen(onBack = { navController.popBackStack() })
                    }
                }
            }
        }
    }
}