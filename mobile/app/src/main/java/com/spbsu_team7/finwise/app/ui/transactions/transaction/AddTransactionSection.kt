package com.spbsu_team7.finwise.app.ui.transactions.transaction

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.*
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.spbsu_team7.finwise.app.ui.util.TextWithOption
import com.spbsu_team7.finwise.app.ui.transactions.transactionOptions.TransactionCategory
import com.spbsu_team7.finwise.app.ui.transactions.transactionOptions.TransactionDate
import com.spbsu_team7.finwise.core.model.Category
import com.spbsu_team7.finwise.core.model.Transaction
import com.spbsu_team7.finwise.core.model.TransactionToSend
import com.spbsu_team7.finwise.core.model.UserIcon
import java.time.Instant

@Composable
fun AddTransactionSection(
    categories: List<Category>,
    sendTransaction: (TransactionToSend) -> Unit
) {
    var sum by remember { mutableStateOf("") }
    val datePickerState = rememberDatePickerState()
    var selectedCategory: Category?  by remember { mutableStateOf(null) }
    var description by remember { mutableStateOf("") }

    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outline,
                    shape = MaterialTheme.shapes.medium
                ),
            shape = MaterialTheme.shapes.medium,
            contentColor = MaterialTheme.colorScheme.onSurface
        ) {
            Column(modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Новая операция", style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.height(20.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth().height(55.dp)
                ) {
                    TextWithOption(name = "Сумма", modifier = Modifier.weight(1f),
                        value = sum,
                        placeholder = "0",
                        valueChange = { text ->
                            val filteredText = text.filter { it.isDigit() }
                            sum = filteredText
                        }
                    )

                    TransactionDate(Modifier.weight(1f), datePickerState)
                }
                TransactionCategory(modifier = Modifier.height(55.dp), categories = categories, selectedCategory, onChange = { selectedCategory = it })

                TextWithOption(name = "Описание", modifier = Modifier.height(55.dp), value = description, "Добавьте описание", valueChange = { description = it })
                AddTransactionButton(modifier = Modifier.height(30.dp),
                    {
                        if (!sum.isEmpty() && !description.isEmpty() && selectedCategory != null && datePickerState.selectedDateMillis != null)
                            sendTransaction(
                                TransactionToSend(
                                    0,
                                    description,
                                    Instant.ofEpochMilli(datePickerState.selectedDateMillis!!),
                                    amount = sum.toInt(),
                                    categoryId = selectedCategory!!.id
                                )
                            )
                    }
                )

            }

        }
    }
}

@Composable
fun AddTransactionButton(modifier: Modifier, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = ButtonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            disabledContainerColor = MaterialTheme.colorScheme.surface,
            disabledContentColor = MaterialTheme.colorScheme.surface,
        ),
        border = null,
    ) {
        Text("Добавить")
    }
}

