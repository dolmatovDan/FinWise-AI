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
import com.spbsu_team7.finwise.app.ui.transactions.transactionOptions.TransactionDate
import java.nio.file.WatchEvent
import java.time.Instant
import java.util.Date
import java.util.Locale

@Composable
fun AddTransactionSection() {
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
                Text("Добавить операцию", style = MaterialTheme.typography.titleMedium)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    var sum by remember { mutableStateOf("") }
                    TextWithOption(name = "Сумма", modifier = Modifier.weight(1f), value = sum, "0",valueChange = { sum = it })
                    val datePickerState = rememberDatePickerState()
                    TransactionDate(Modifier.weight(1f), datePickerState)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    ExposedDropdownMenuBoxSample(modifier = Modifier.weight(1f))
                    OutlinedTextField(
                        value = "",
                        onValueChange = {},
                        label = { Text("Комментарий") },
                        modifier = Modifier.weight(1f)
                    )
                }
                Button(
                    onClick = { /* placeholder */ },
                    modifier = Modifier
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outline,
                            shape = MaterialTheme.shapes.medium
                        )
                        .height(30.dp)
                        .fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    colors = ButtonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        disabledContainerColor = MaterialTheme.colorScheme.surface,
                        disabledContentColor = MaterialTheme.colorScheme.surface,
                    )
                ) {
                    Text("Добавить")
                }
            }

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExposedDropdownMenuBoxSample(modifier: Modifier = Modifier) {
    OutlinedTextField(
        value = "Выбрать",
        onValueChange = {},
        label = { Text("Категории") },
        modifier = modifier,
        readOnly = true,
        trailingIcon = {  }
    )
}


fun convertMillisToDate(millis: Long): String {
    val formatter = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    return formatter.format(Date(millis))
}
