package com.example.pocketmoney.ui.transactions

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.pocketmoney.domain.models.TransactionType // ИСПРАВЛЕНО: Добавили импорт Enum типа транзакции
import com.example.pocketmoney.ui.home.TransactionItem
import org.koin.androidx.compose.koinViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionsScreen(
    onBack: () -> Unit,
    viewModel: TransactionsViewModel = koinViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    // Состояние для открытия/закрытия диалога календаря
    var showDatePicker by remember { mutableStateOf(false) }

    // Форматтер для красивого отображения дат пользователю
    val dateFormatter = remember { SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("История операций") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                },
                actions = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(Icons.Default.DateRange, contentDescription = "Фильтр по дате")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // --- ИСПРАВЛЕНО: Панель фильтров по типу транзакции (Доход / Расход) ---
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Кнопка "Все"
                item {
                    FilterChip(
                        selected = state.selectedType == null,
                        onClick = { viewModel.selectType(null) },
                        label = { Text("Все") }
                    )
                }
                // Кнопка "Доходы"
                item {
                    FilterChip(
                        selected = state.selectedType == TransactionType.INCOME,
                        onClick = { viewModel.selectType(TransactionType.INCOME) },
                        label = { Text("Доходы") }
                    )
                }
                // Кнопка "Расходы"
                item {
                    FilterChip(
                        selected = state.selectedType == TransactionType.EXPENSE,
                        onClick = { viewModel.selectType(TransactionType.EXPENSE) },
                        label = { Text("Расходы") }
                    )
                }
            }

            // Индикатор активного фильтра дат
            if (state.selectedDateRange != null) {
                val startStr = dateFormatter.format(Date(state.selectedDateRange!!.first))
                val endStr = dateFormatter.format(Date(state.selectedDateRange!!.second))

                InputChip(
                    selected = true,
                    onClick = { viewModel.selectDateRange(null, null) }, // Сброс при клике
                    label = { Text("Период: $startStr - $endStr") },
                    trailingIcon = {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Сбросить фильтр дат",
                            modifier = Modifier.size(16.dp)
                        )
                    },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )
            }

            // --- Список транзакций ---
            if (state.transactions.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Ничего не найдено", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.transactions, key = { it.id }) { transaction ->
                        TransactionItem(
                            transaction = transaction,
                            currency = state.currency
                        )
                    }
                }
            }
        }
    }

    // Диалоговое окно выбора диапазона дат (Material 3 DateRangePicker)
    if (showDatePicker) {
        val dateRangePickerState = rememberDateRangePickerState()

        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.selectDateRange(
                            dateRangePickerState.selectedStartDateMillis,
                            dateRangePickerState.selectedEndDateMillis
                        )
                        showDatePicker = false
                    },
                    enabled = dateRangePickerState.selectedStartDateMillis != null &&
                            dateRangePickerState.selectedEndDateMillis != null
                ) {
                    Text("Применить")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Отмена")
                }
            }
        ) {
            DateRangePicker(
                state = dateRangePickerState,
                title = { Text(text = "Выберите период", modifier = Modifier.padding(16.dp)) },
                headline = { Text(text = "Фильтр операций", modifier = Modifier.padding(16.dp)) },
                showModeToggle = false,
                modifier = Modifier.weight(1f)
            )
        }
    }
}