package com.spbsu_team7.finwise.app.ui.transactions.transaction

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.*
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.spbsu_team7.finwise.app.ui.util.TextWithOption
import com.spbsu_team7.finwise.app.ui.transactions.transactionOptions.TransactionCategory
import com.spbsu_team7.finwise.app.ui.transactions.transactionOptions.TransactionDate
import com.spbsu_team7.finwise.core.model.Category
import com.spbsu_team7.finwise.core.model.TransactionToSend
import kotlinx.coroutines.launch
import java.time.Instant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionSection(
    expanded: Boolean,
    onClick: () -> Unit,
    categories: List<Category>,
    sendTransaction: (TransactionToSend) -> Unit
) {

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()


    var sum by remember { mutableStateOf("") }
    val datePickerState = rememberDatePickerState()
    var selectedCategory: Category?  by remember { mutableStateOf(null) }
    var description by remember { mutableStateOf("") }



    if (expanded) {
        ModalBottomSheet(
            modifier = Modifier.fillMaxHeight(),
            onDismissRequest = onClick,
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface
        ) {

                    Box(
                        modifier = Modifier.fillMaxSize().padding(16.dp)
                    ) {
                        Column(
                            modifier = Modifier,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                "Новая операция", style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier.height(25.dp)
                            )
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.fillMaxWidth().height(60.dp)
                            ) {
                                TextWithOption(
                                    name = "Сумма", modifier = Modifier.weight(1f),
                                    value = sum,
                                    placeholder = "0",
                                    valueChange = { text ->
                                        val filteredText = text.filter { it.isDigit() || it == '-' }
                                        sum = filteredText
                                    }
                                )
                                TransactionDate(Modifier.weight(1f), datePickerState)
                            }
                            TransactionCategory(
                                modifier = Modifier.height(60.dp),
                                categories = categories,
                                selectedCategory,
                                onChange = { selectedCategory = it })

                            TextWithOption(
                                name = "Описание",
                                modifier = Modifier.height(60.dp),
                                value = description,
                                "Добавьте описание",
                                valueChange = { description = it })
                        }

                        AddTransactionButton(
                            modifier = Modifier
                                .height(40.dp).align(Alignment.BottomCenter),
                            {
                                if (!sum.isEmpty() && !description.isEmpty() && selectedCategory != null && datePickerState.selectedDateMillis != null) {
                                    sendTransaction(
                                        TransactionToSend(
                                            0,
                                            description,
                                            Instant.ofEpochMilli(datePickerState.selectedDateMillis!!),
                                            amount = sum.toInt(),
                                            categoryId = selectedCategory!!.id
                                        )
                                    )
                                    scope.launch { sheetState.hide() }
                                        .invokeOnCompletion {
                                            if (!sheetState.isVisible)
                                                onClick()
                                        }
                                }
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

