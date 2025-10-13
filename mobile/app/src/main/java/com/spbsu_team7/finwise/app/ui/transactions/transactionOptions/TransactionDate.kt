package com.spbsu_team7.finwise.app.ui.transactions.transactionOptions

import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.spbsu_team7.finwise.app.ui.transactions.transaction.convertMillisToDate
import java.time.Instant

@Composable
fun TransactionDate(modifier: Modifier, datePickerState: DatePickerState) {

    var showModal by remember { mutableStateOf(false) }
    val selectedDate = datePickerState.selectedDateMillis?.let {
        convertMillisToDate(it)
    } ?: ""

    TextWithOption(
        "Дата",
        modifier,
        selectedDate.toString(),
        convertMillisToDate(Instant.now().toEpochMilli()),
        onClick = { showModal = !showModal },
        trailingIcon = {
            Box(contentAlignment = Alignment.CenterStart) {
                Icon(
                    modifier = Modifier,
                    imageVector = Icons.Default.DateRange,
                    contentDescription = "Выбрать дату"
                )
            }
        },
    )
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
            }
        ) {
            DatePicker(
                state = datePickerState
            )
        }
    }
}