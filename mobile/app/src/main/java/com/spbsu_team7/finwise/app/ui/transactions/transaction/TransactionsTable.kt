package com.spbsu_team7.finwise.app.ui.transactions.transaction

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.spbsu_team7.finwise.app.UiState
import com.spbsu_team7.finwise.core.model.Transaction

import com.spbsu_team7.finwise.app.ui.theme.ExpenseRed
import com.spbsu_team7.finwise.app.ui.theme.IncomeGreen
import kotlin.math.absoluteValue


@Composable
fun TransactionsTable(transactions: List<Transaction>) {

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("Операции", style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(horizontal = 7.dp))
        LazyColumn(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            items(items = transactions) { ts ->
                TransactionRow(transaction = ts)
            }
        }
    }

}
//        Column(modifier = Modifier.padding(16.dp)) {
//            Text("Операции", style = MaterialTheme.typography.titleLarge)
//            Spacer(modifier = Modifier.height(12.dp))
//            HorizontalDivider()
//            Spacer(modifier = Modifier.height(8.dp))
//            LazyColumn(modifier = Modifier.heightIn(max = 200.dp)) {
//                items(sampleTransactions) { tx ->
//                    TransactionRow(transaction = tx)
//                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
//                }
//            }
//        }



@Composable
fun TransactionRow(transaction: Transaction) {
    Surface(
        modifier = Modifier.border(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outline,
            shape = MaterialTheme.shapes.medium
        ).height(60.dp),
        shape = MaterialTheme.shapes.medium,
        contentColor = MaterialTheme.colorScheme.onSurface
    ) {
        val textColor = if (transaction.amount > 0) IncomeGreen else ExpenseRed
        Row (
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        )
        {
            Icon (
                imageVector = transaction.category.icon,
                contentDescription = transaction.category.name,
                tint = transaction.category.color,
                modifier = Modifier.padding(vertical = 10.dp).weight(0.2f)
            )
            Column (
                modifier = Modifier.weight(0.5f),
                verticalArrangement = Arrangement.SpaceAround
            ) {
                Row {
                    Text(text = transaction.name, style = MaterialTheme.typography.bodyMedium)
                }
                Row {
                    Text(text = transaction.category.name, style = MaterialTheme.typography.labelSmall)
                }
            }
            val sign = if (transaction.amount < 0) "- " else "+ "
            Text (
                textAlign = TextAlign.Right,
                text = sign + transaction.amount.absoluteValue.toString() + " ₽",
                color = textColor,
                modifier = Modifier.weight(0.3f).padding(end = 20.dp),
            )
        }

    }
}

//
//@Composable
//fun TransactionRow(transaction: Transaction) {
//    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
//        val textColor = if (transaction.amount > 0) IncomeGreen else ExpenseRed
//        Text(transaction.date.toString(), modifier = Modifier.weight(0.3f))
//        Text(transaction.amount.toString() + " $", modifier = Modifier.weight(0.3f),
//            color = textColor)
//        Text(transaction.category.name, modifier = Modifier.weight(0.2f))
//    }
//}
