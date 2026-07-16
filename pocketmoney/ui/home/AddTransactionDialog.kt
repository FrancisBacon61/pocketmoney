package com.example.pocketmoney.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.pocketmoney.R
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
    var amountText by rememberSaveable { mutableStateOf("") }
    var commentText by rememberSaveable { mutableStateOf("") }
    var isIncome by rememberSaveable { mutableStateOf(false) }

    var menuExpanded by rememberSaveable { mutableStateOf(false) }

    var selectedCategoryId by rememberSaveable(categories, initialCategory) {
        mutableStateOf(initialCategory?.id ?: categories.firstOrNull()?.id)
    }

    val selectedCategory = remember(selectedCategoryId, categories) {
        categories.find { it.id == selectedCategoryId }
    }

    var isCustomCategory by rememberSaveable { mutableStateOf(false) }
    var customCategoryName by rememberSaveable { mutableStateOf("") }

    val amount = amountText.toDoubleOrNull()
    val isAmountValid = amount != null && amount > 0
    val isAmountError = amountText.isNotEmpty() && !isAmountValid

    val isCategoryValid = (!isCustomCategory && selectedCategory != null) ||
            (isCustomCategory && customCategoryName.isNotBlank())

    val isSaveEnabled = isAmountValid && isCategoryValid

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (isIncome) stringResource(R.string.new_income) else stringResource(R.string.new_expense)) },
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
                        label = { Text(stringResource(R.string.expense)) }
                    )
                    FilterChip(
                        selected = isIncome,
                        onClick = { isIncome = true },
                        label = { Text(stringResource(R.string.income)) }
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
                    label = { Text(stringResource(R.string.amount_label, currency)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    isError = isAmountError,
                    supportingText = {
                        if (isAmountError) {
                            Text(text = stringResource(R.string.enter_number), color = MaterialTheme.colorScheme.error)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                ExposedDropdownMenuBox(
                    expanded = menuExpanded,
                    onExpandedChange = { menuExpanded = !menuExpanded }
                ) {
                    val categoryFieldText = if (isCustomCategory) {
                        stringResource(R.string.custom_category_title)
                    } else {
                        "${selectedCategory?.icon ?: "🏷️"} ${selectedCategory?.name ?: stringResource(
                            R.string.no_selected
                        )}"
                    }

                    OutlinedTextField(
                        value = categoryFieldText,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(stringResource(R.string.category_label)) },
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
                                    selectedCategoryId = category.id
                                    isCustomCategory = false
                                    menuExpanded = false
                                }
                            )
                        }

                        HorizontalDivider()

                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.custom_category_prompt)) },
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
                        label = { Text(stringResource(R.string.new_category_name)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                OutlinedTextField(
                    value = commentText,
                    onValueChange = { commentText = it },
                    label = { Text(stringResource(R.string.comment)) },
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
                Text(stringResource(R.string.save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}