package com.spbsu_team7.finwise.ui.transactions.transaction

import android.util.Log
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import com.spbsu_team7.finwise.ui.theme.ExpenseRed
import com.spbsu_team7.finwise.ui.theme.IncomeGreen

data class Transaction(val date: String, val amount: String, val category: String, val note: String)

private val sampleTransactions = listOf(
    Transaction("04.10.2025", "+ 20 000 ₽", "Стипендия", "—"),
    Transaction("05.10.2025", "- 200 ₽", "Питание", "—"),
    Transaction("05.10.2025", "- 150 ₽", "Транспорт", "—")
)

@Composable
fun TransactionsTable() {
    Surface(
        modifier = Modifier.fillMaxWidth().border(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outline,
            shape = MaterialTheme.shapes.medium
        ),
        shape = MaterialTheme.shapes.medium,
        contentColor = MaterialTheme.colorScheme.onSurface
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Операции", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(8.dp))
            LazyColumn(modifier = Modifier.heightIn(max = 200.dp)) {
                items(sampleTransactions) { tx ->
                    TransactionRow(transaction = tx)
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                }
            }
        }
    }
}

@Composable
fun TransactionRow(transaction: Transaction) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        val textColor = if (transaction.amount.get(0) == '+') IncomeGreen else ExpenseRed
        Text(transaction.date, modifier = Modifier.weight(0.3f))
        Text(transaction.amount, modifier = Modifier.weight(0.3f),
            color = textColor)
        Text(transaction.category, modifier = Modifier.weight(0.2f))
    }
}
