package com.example.pocketmoney.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    viewModel: SettingsViewModel = koinViewModel()
) {
    val currency by viewModel.currency.collectAsState()
    val isHidden by viewModel.isBalanceHidden.collectAsState()

    // ДОБАВЛЕНО: Подписываемся на новые состояния синхронизации валют
    val lastUpdate by viewModel.lastCurrencyUpdate.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()

    // Форматтер времени для отображения пользователю
    val dateFormatter = remember { SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Настройки") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // --- 1. Скрыть баланс ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Скрывать баланс на главном")
                Switch(checked = isHidden, onCheckedChange = { viewModel.toggleBalanceHidden(it) })
            }

            HorizontalDivider() // В M3 используется HorizontalDivider вместо старого Divider

            // --- 2. Выбор валюты счета ---
            Text("Валюта счета", style = MaterialTheme.typography.titleMedium)
            val currencies = listOf("RUB", "USD", "EUR", "KZT")

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                currencies.forEach { code ->
                    FilterChip(
                        selected = currency == code,
                        onClick = { viewModel.setCurrency(code) },
                        label = { Text(code) }
                    )
                }
            }

            // --- 3. ДОБАВЛЕНО ПО ТЗ: Блок обновления курсов валют ---
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Курсы валют", style = MaterialTheme.typography.labelLarge)
                        Text(
                            text = if (lastUpdate == 0L) "Ещё не обновлялись" else "Обновлено: ${dateFormatter.format(Date(lastUpdate))}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    if (isRefreshing) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                    } else {
                        IconButton(onClick = { viewModel.refreshRates() }) {
                            Icon(Icons.Default.Refresh, contentDescription = "Обновить курсы")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // --- 4. Очистка данных ---
            Button(
                onClick = { viewModel.clearAllData() },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Очистить все данные", color = MaterialTheme.colorScheme.onError)
            }
        }
    }
}