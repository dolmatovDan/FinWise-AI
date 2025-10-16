package com.spbsu_team7.finwise.app.ui.transactions.transactionOptions

import android.icu.text.SimpleDateFormat
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerColors
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerFormatter
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.spbsu_team7.finwise.app.ui.theme.FinanceTheme
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

@Composable
fun TransactionDate(modifier: Modifier, datePickerState: DatePickerState) {

    var showModal by remember { mutableStateOf(false) }
    val selectedDate = datePickerState.selectedDateMillis?.let {
        convertMillisToDate(it)
    } ?: ""


    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Text(
            text = "Дата",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(horizontal = 0.dp)
        )
        Surface(
            modifier = Modifier.clickable(
                onClick = { showModal = !showModal }
            ),
            shape = MaterialTheme.shapes.small,
            color = MaterialTheme.colorScheme.secondaryContainer
        ) {
            Row(
                modifier = Modifier.fillMaxSize().padding(horizontal = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Box(contentAlignment = Alignment.CenterStart) {
                    Icon(
                        modifier = Modifier,
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "выбрать дату"
                    )
                }
                if (selectedDate.isEmpty()) {
                    Text(
                        convertMillisToDate(Instant.now().toEpochMilli()),
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
                else (
                    Text(text = selectedDate, style = MaterialTheme.typography.bodyMedium)
                )
            }
        }
//        TextWithOption(
//            "Дата",
//            modifier,
//            selectedDate.toString(),
//            convertMillisToDate(Instant.now().toEpochMilli()),
//            onClick = { showModal = !showModal },
//            trailingIcon = {
//                Box(contentAlignment = Alignment.CenterStart) {
//                    Icon(
//                        modifier = Modifier,
//                        imageVector = Icons.Default.DateRange,
//                        contentDescription = "Выбрать дату"
//                    )
//                }
//            },
//        )
    }
    if (showModal) {
        DatePickerDialog(
            onDismissRequest = { showModal = false },
            confirmButton = {
                Button(onClick = { showModal = false }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showModal = false }) {
                    Text("Отмена")
                }
            },
            colors = datePickerDialogColor()
        ) {
            DatePicker(
                title = {

                },
                headline = {
                    Text("Выберите дату",
                        style = MaterialTheme.typography.headlineLarge,
                        modifier = Modifier.padding(10.dp))
                },
                state = datePickerState,
                colors = datePickerColor(),
            )
        }
    }
}

@Composable
fun datePickerColor() =
    DatePickerDefaults.colors().copy(
        containerColor = MaterialTheme.colorScheme.surface,
        )
@Composable
fun datePickerDialogColor() =
    DatePickerDefaults.colors().copy(
        containerColor = MaterialTheme.colorScheme.surface,
    )

@Preview
@Composable
fun previewDatePicker(){
    FinanceTheme {
        DatePickerDialog(
            onDismissRequest = {},
            confirmButton = {
                Button(onClick = {}) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = {}) {
                    Text("Отмена")
                }
            },
            colors = datePickerDialogColor(),
        ) {
            DatePicker(
                title = {

                },
                headline = {
                    Text("Выберите дату",
                        style = MaterialTheme.typography.headlineLarge,
                        modifier = Modifier.padding(10.dp))
                },
                state = rememberDatePickerState(),
                colors = datePickerColor()
            )
        }
    }
}

fun convertMillisToDate(millis: Long): String {
    val formatter = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    return formatter.format(Date(millis))
}
