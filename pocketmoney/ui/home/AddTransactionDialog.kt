package com.example.pocketmoney.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState // ДОБАВИЛИ ДЛЯ СКРОЛЛА
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll // ДОБАВИЛИ ДЛЯ СКРОЛЛА
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionDialog(
    currency: String,
    onDismiss: () -> Unit,
    onSave: (amount: Double, comment: String, isIncome: Boolean) -> Unit
) {
    var amountText by remember { mutableStateOf("") }
    var commentText by remember { mutableStateOf("") }
    var isIncome by remember { mutableStateOf(false) }

    val amount = amountText.toDoubleOrNull()
    val isAmountValid = amount != null && amount > 0
    val isInputError = amountText.isNotEmpty() && !isAmountValid

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (isIncome) "Новый доход" else "Новый расход") },
        text = {
            // ИСПРАВЛЕНО: Добавили вертикальный скролл для поддержки горизонтального экрана
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()) // Теперь всё внутри будет плавно скроллиться!
            ) {
                // Переключатель Доход/Расход
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    FilterChip(
                        selected = !isIncome,
                        onClick = { isIncome = false },
                        label = { Text("Расход") }
                    )
                    FilterChip(
                        selected = isIncome,
                        onClick = { isIncome = true },
                        label = { Text("Доход") }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Поле для суммы с валидацией
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { input ->
                        amountText = input.replace(',', '.')
                    },
                    label = { Text("Сумма ($currency)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    isError = isInputError,
                    supportingText = {
                        if (isInputError) {
                            Text(
                                text = "Введите число больше 0",
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Поле для комментария
                OutlinedTextField(
                    value = commentText,
                    onValueChange = { commentText = it },
                    label = { Text("Комментарий") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val finalAmount = amountText.toDoubleOrNull()
                    if (finalAmount != null) {
                        onSave(finalAmount, commentText, isIncome)
                    }
                },
                enabled = isAmountValid
            ) {
                Text("Сохранить")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена")
            }
        }
    )
}