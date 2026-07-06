package com.example.pocketmoney.ui.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.pocketmoney.domain.models.CurrencyRate // ИСПРАВЛЕНО: Импортируем доменную модель курсов
import com.example.pocketmoney.domain.models.Transaction
import com.example.pocketmoney.domain.models.TransactionType
import com.example.pocketmoney.ui.theme.PocketMoneyTheme
import org.koin.androidx.compose.koinViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = koinViewModel(),
    onNavigateToTransactions: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    // Получаем состояние экрана (Загрузка / Контент / Ошибка)
    val uiState by viewModel.uiState.collectAsState()
    // Получаем состояние всплывающих диалогов
    val dialogState by viewModel.dialogState.collectAsState()

    PocketMoneyTheme {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Мои финансы") },
                    navigationIcon = {
                        IconButton(onClick = onNavigateToSettings) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Настройки"
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = onNavigateToTransactions) {
                            Icon(
                                imageVector = Icons.Default.History,
                                contentDescription = "История транзакций"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            },
            floatingActionButton = {
                FloatingActionButton(onClick = { viewModel.showAddDialog() }) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Добавить")
                }
            }
        ) { paddingValues ->

            // ИСПРАВЛЕНО: Реализуем LCE паттерн через разбор состояний sealed-интерфейса
            when (val state = uiState) {
                is HomeUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is HomeUiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                        Text(text = "Ошибка: ${state.message}", color = MaterialTheme.colorScheme.error)
                    }
                }
                is HomeUiState.Content -> {
                    // Вытаскиваем чистые данные экрана из успешного состояния
                    val data = state.state

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                            .padding(horizontal = 16.dp)
                    ) {
                        Spacer(modifier = Modifier.height(16.dp))

                        // Карточка баланса
                        BalanceCard(
                            balance = data.totalBalance,
                            currency = data.currency,
                            isHidden = data.isBalanceHidden,
                            onClick = { viewModel.toggleBalanceVisibility() }
                        )

                        // Блок курсов валют
                        if (data.rates.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(16.dp))
                            CurrencyRatesSection(rates = data.rates)
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Text(
                            text = "Последние операции",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        if (data.transactions.isEmpty()) {
                            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                                Text(text = "Список пуст", color = Color.Gray)
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(
                                    items = data.transactions,
                                    key = { it.id }
                                ) { transaction ->
                                    Box(modifier = Modifier
                                        .fillMaxWidth()
                                        .combinedClickable(
                                            onClick = { /* Клик при необходимости */ },
                                            onLongClick = { viewModel.confirmDeleteTransaction(transaction) }
                                        )
                                    ) {
                                        TransactionItem(transaction = transaction, currency = data.currency)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // ВСПЛЫВАЮЩИЕ ОКНА (берутся из независимого dialogState)
        val currentCurrency = (uiState as? HomeUiState.Content)?.state?.currency ?: "RUB"

        if (dialogState.isAddDialogVisible) {
            AddTransactionDialog(
                currency = currentCurrency,
                onDismiss = { viewModel.hideAddDialog() },
                onSave = { amount, comment, isIncome ->
                    viewModel.addTransaction(amount, comment, isIncome)
                }
            )
        }

        dialogState.transactionToDelete?.let { transaction ->
            AlertDialog(
                onDismissRequest = { viewModel.dismissDeleteDialog() },
                title = { Text("Удаление операции") },
                text = {
                    Text("Вы уверены, что хотите удалить запись " +
                            (if (transaction.comment.isNotEmpty()) "\"${transaction.comment}\"" else "на сумму ${transaction.amount} $currentCurrency") +
                            "?")
                },
                confirmButton = {
                    TextButton(
                        onClick = { viewModel.deleteTransaction(transaction) },
                        colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("Удалить")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { viewModel.dismissDeleteDialog() }) {
                        Text("Отмена")
                    }
                }
            )
        }
    }
}

// ИСПРАВЛЕНО: Теперь принимает доменный CurrencyRate вместо Entity из базы данных
@Composable
fun CurrencyRatesSection(rates: List<CurrencyRate>) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Курсы валют",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(end = 16.dp)
        ) {
            items(items = rates, key = { it.code }) { rate ->
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = rate.code,
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Text(
                            text = String.format(Locale.US, "%.2f", rate.rate),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BalanceCard(
    balance: Double,
    currency: String,
    isHidden: Boolean,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary
        )
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Общий баланс",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
            )

            val displayAmount = if (isHidden) "****" else String.format(Locale.US, "%.2f", balance)

            Text(
                text = "$displayAmount $currency",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

@Composable
fun TransactionItem(transaction: Transaction, currency: String) {
    val dateString = remember(transaction.date) {
        val sdf = SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault())
        sdf.format(Date(transaction.date))
    }

    val incomeColor = Color(0xFF2E7D32)
    val expenseColor = Color(0xFFC62828)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                if (transaction.comment.isNotEmpty()) {
                    Text(
                        text = transaction.comment,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                Text(
                    text = dateString,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            }

            val isIncome = transaction.type == TransactionType.INCOME
            val formattedAmount = String.format(Locale.US, "%.2f", transaction.amount)

            Text(
                text = "${if (isIncome) "+" else "-"} $formattedAmount $currency",
                style = MaterialTheme.typography.titleMedium,
                color = if (isIncome) incomeColor else expenseColor
            )
        }
    }
}