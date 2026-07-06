package com.example.pocketmoney.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionDialog(
    currency: String, // ИСПРАВЛЕНО: передаем валюту динамически
    onDismiss: () -> Unit,
    onSave: (amount: Double, comment: String, isIncome: Boolean) -> Unit
) {
    var amountText by remember { mutableStateOf("") }
    var commentText by remember { mutableStateOf("") }
    var isIncome by remember { mutableStateOf(false) }

    // ВАЛИДАЦИЯ: Проверяем, является ли ввод корректным числом больше нуля
    val amount = amountText.toDoubleOrNull()
    val isAmountValid = amount != null && amount > 0

    // Показывать ошибку только если пользователь уже начал вводить текст, но он неверный
    val isInputError = amountText.isNotEmpty() && !isAmountValid

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (isIncome) "Новый доход" else "Новый расход") },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
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
                        // Небольшое UX-улучшение: заменяем запятую на точку, чтобы Double ко парсился без проблем
                        amountText = input.replace(',', '.')
                    },
                    label = { Text("Сумма ($currency)") }, // ИСПРАВЛЕНО: подставляем динамическую валюту
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    isError = isInputError, // Включает красную рамку Material 3 при ошибке
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
                    // Берем текущее число. Так как кнопка активна (enabled = isAmountValid),
                    // мы на 100% уверены, что здесь число корректное и больше нуля.
                    val finalAmount = amountText.toDoubleOrNull()
                    if (finalAmount != null) {
                        onSave(finalAmount, commentText, isIncome)
                    }
                },
                enabled = isAmountValid // Кнопка по-прежнему блокируется на основе внешней валидации
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