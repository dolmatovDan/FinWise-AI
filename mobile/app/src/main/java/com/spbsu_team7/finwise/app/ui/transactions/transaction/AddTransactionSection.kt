package com.spbsu_team7.finwise.app.ui.transactions.transaction

import android.icu.text.SimpleDateFormat
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import com.spbsu_team7.finwise.app.ui.transactions.transactionOptions.TextWithOption
import com.spbsu_team7.finwise.app.ui.transactions.transactionOptions.TransactionCategory
import com.spbsu_team7.finwise.app.ui.transactions.transactionOptions.TransactionDate
import com.spbsu_team7.finwise.core.model.Category
import java.nio.file.WatchEvent
import java.time.Instant
import java.util.Date
import java.util.Locale

@Composable
fun AddTransactionSection(categories: List<Category>) {
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
                    var sum by remember { mutableStateOf("") }
                    TextWithOption(name = "Сумма", modifier = Modifier.weight(1f), value = sum, "0", valueChange = { sum = it })
                    val datePickerState = rememberDatePickerState()
                    TransactionDate(Modifier.weight(1f), datePickerState)
                }
                TransactionCategory(modifier = Modifier.height(55.dp), categories = categories)
                var description by remember { mutableStateOf("") }
                TextWithOption(name = "Описание", modifier = Modifier.height(55.dp), value = description, "Добавьте описание", valueChange = { description = it })
                AddTransactionButton(modifier = Modifier.height(30.dp))
            }

        }
    }
}

@Composable
fun AddTransactionButton(modifier: Modifier) {
    Button(
        onClick = { /* placeholder */ },
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

