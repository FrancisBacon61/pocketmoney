package com.example.pocketmoney.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.pocketmoney.domain.models.Category

// Регулярное выражение: разрешает только цифры, максимум одну точку и до 2 знаков после запятой
private val MoneyInputRegex = Regex("""^\d*(?:\.\d{0,2})?$""")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionDialog(
    currency: String,
    categories: List<Category>,
    initialCategory: Category? = null,
    onDismiss: () -> Unit,
    onSave: (amount: Double, comment: String, isIncome: Boolean, categoryId: Long?, newCategoryName: String?) -> Unit
) {
    var amountText by remember { mutableStateOf("") }
    var commentText by remember { mutableStateOf("") }
    var isIncome by remember { mutableStateOf(false) }

    var menuExpanded by remember { mutableStateOf(false) }

    var selectedCategory by remember(categories, initialCategory) {
        mutableStateOf(initialCategory ?: categories.firstOrNull())
    }

    var isCustomCategory by remember { mutableStateOf(false) }
    var customCategoryName by remember { mutableStateOf("") }

    val amount = amountText.toDoubleOrNull()
    val isAmountValid = amount != null && amount > 0
    val isAmountError = amountText.isNotEmpty() && !isAmountValid

    val isCategoryValid = (!isCustomCategory && selectedCategory != null) ||
            (isCustomCategory && customCategoryName.isNotBlank())

    val isSaveEnabled = isAmountValid && isCategoryValid

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (isIncome) "Новый доход" else "Новый расход") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
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

                OutlinedTextField(
                    value = amountText,
                    onValueChange = { input ->
                        val formatted = input.replace(',', '.')
                        if (formatted.isEmpty() || formatted.matches(MoneyInputRegex)) {
                            amountText = formatted
                        }
                    },
                    label = { Text("Сумма ($currency)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    isError = isAmountError,
                    supportingText = {
                        if (isAmountError) {
                            Text(text = "Введите корректное число", color = MaterialTheme.colorScheme.error)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                ExposedDropdownMenuBox(
                    expanded = menuExpanded,
                    onExpandedChange = { menuExpanded = !menuExpanded }
                ) {
                    val categoryFieldText = if (isCustomCategory) {
                        "➕ Своя категория..."
                    } else {
                        "${selectedCategory?.icon ?: "🏷️"} ${selectedCategory?.name ?: "Не выбрана"}"
                    }

                    OutlinedTextField(
                        value = categoryFieldText,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Категория") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = menuExpanded) },
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(
                                type = ExposedDropdownMenuAnchorType.PrimaryNotEditable,
                                enabled = true
                            )
                    )

                    ExposedDropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false }
                    ) {
                        categories.forEach { category ->
                            DropdownMenuItem(
                                text = { Text("${category.icon}  ${category.name}") },
                                onClick = {
                                    selectedCategory = category
                                    isCustomCategory = false
                                    menuExpanded = false
                                }
                            )
                        }

                        HorizontalDivider()

                        DropdownMenuItem(
                            text = { Text("➕ Добавить свою...") },
                            onClick = {
                                isCustomCategory = true
                                menuExpanded = false
                            }
                        )
                    }
                }

                if (isCustomCategory) {
                    OutlinedTextField(
                        value = customCategoryName,
                        onValueChange = { customCategoryName = it },
                        label = { Text("Название новой категории") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

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
                    if (isSaveEnabled) {
                        onSave(
                            amount,
                            commentText.trim(),
                            isIncome,
                            if (isCustomCategory) null else selectedCategory?.id,
                            if (isCustomCategory) customCategoryName.trim() else null
                        )
                    }
                },
                enabled = isSaveEnabled
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