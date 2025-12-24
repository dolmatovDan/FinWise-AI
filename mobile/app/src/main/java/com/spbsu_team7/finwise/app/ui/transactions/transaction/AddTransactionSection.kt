package com.spbsu_team7.finwise.app.ui.transactions.transaction

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.spbsu_team7.finwise.app.ui.camera.StartCamera
import com.spbsu_team7.finwise.app.ui.theme.FinanceTheme
import com.spbsu_team7.finwise.app.ui.util.TextWithOption
import com.spbsu_team7.finwise.app.ui.transactions.transactionOptions.TransactionCategory
import com.spbsu_team7.finwise.app.ui.transactions.transactionOptions.TransactionDate
import com.spbsu_team7.finwise.core.model.Category
import com.spbsu_team7.finwise.core.model.Transaction
import com.spbsu_team7.finwise.core.model.TransactionToSend
import kotlinx.coroutines.launch
import java.time.Instant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionSection(
    expanded: Boolean,
    onClick: () -> Unit,
    categories: List<Category>,
    sendTransaction: (TransactionToSend) -> Unit,
    onTakenPhoto: (Uri) -> List<Transaction>
) {

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    var addDialog by remember { mutableStateOf(false) }


    var transactions by remember { mutableStateOf(emptyList<Transaction>()) }

    AddDialog(addDialog, { addDialog = false }, transactions)

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

                    val sheetStateCamera = rememberModalBottomSheetState(skipPartiallyExpanded = true)
                    var expandedCamera by remember { mutableStateOf(false) }

                    if (expandedCamera) {
                        ModalBottomSheet(
                            modifier = Modifier.fillMaxHeight().padding(top = 50.dp),
                            onDismissRequest = { expandedCamera = !expandedCamera },
                            sheetState = sheetStateCamera,
                            containerColor = MaterialTheme.colorScheme.surface
                        ) {
                            StartCamera(
                                {
                                    scope.launch { sheetStateCamera.hide() }
                                        .invokeOnCompletion {
                                            if (!sheetStateCamera.isVisible)
                                                expandedCamera = !expandedCamera
                                        }

                                },
                                { uri ->
                                    addDialog = true
                                    transactions = onTakenPhoto(uri)
                                }
                            )
                        }
                    }




                    Box(
                        modifier = Modifier.fillMaxSize().padding(16.dp)
                    ) {
                        Column(
                            modifier = Modifier,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Row (
                                modifier = Modifier.height(35.dp).fillMaxWidth(),
                                horizontalArrangement = Arrangement.Absolute.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "Новая операция", style = MaterialTheme.typography.titleLarge,
                                    modifier = Modifier.width(200.dp).padding(0.dp)
                                )
                                Button (
                                    modifier = Modifier.width(150.dp).padding(0.dp),
                                    shape = MaterialTheme.shapes.medium,
                                    onClick = { expandedCamera = true }
                                ) {
                                    Text("Сканировать",)
                                }
                            }
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
fun AddDialog(opened: Boolean, close: () -> Unit, transactions: List<Transaction> ) {
    AddDialogContent(opened, close, transactions)
}

@Composable
fun AddDialogContent(opened: Boolean, close: () -> Unit, transactions: List<Transaction> ) {
    if (opened) {
        Dialog(
            onDismissRequest = close
        ) {
            Surface(
                modifier = Modifier.height(600.dp).width(350.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                Column() {
                    Text(
                        modifier = Modifier.fillMaxWidth().height(40.dp).padding(top = 10.dp, bottom = 0.dp),
                        textAlign = TextAlign.Center,
                        text = "Верно ли распознано?",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Surface(modifier = Modifier.height(500.dp)) {
                        TransactionsTable(transactions)
                    }
                    Row(
                        modifier = Modifier.height(50.dp).padding(bottom = 0.dp, start = 5.dp, end = 5.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            modifier = Modifier.fillMaxHeight().weight(1f).padding(0.dp),
                            onClick = {
                                close()
                            }
                        ) {
                            Text("Нет")
                        }
                        Button(
                            modifier = Modifier.fillMaxHeight().weight(1f).padding(0.dp),
                            onClick = {}
                        ) {
                            Text("Да")
                        }
                    }
                }
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


@Preview
@Composable
fun AddDialogPreview() {
    FinanceTheme {
        AddDialogContent(true, {},
            listOf(Transaction(1, "Good", Instant.now(), 1000, Category(1, "aaa", Icons.Default.Star, Color(255, 0, 0))))
        )
    }
}
