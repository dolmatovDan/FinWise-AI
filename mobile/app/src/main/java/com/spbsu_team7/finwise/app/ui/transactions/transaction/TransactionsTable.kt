package com.spbsu_team7.finwise.app.ui.transactions.transaction

import android.util.Log
import androidx.activity.viewModels
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.spbsu_team7.finwise.app.UiState
import com.spbsu_team7.finwise.app.ViewModel
import com.spbsu_team7.finwise.core.model.Transaction

import com.spbsu_team7.finwise.app.ui.theme.ExpenseRed
import com.spbsu_team7.finwise.app.ui.theme.IncomeGreen
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.chrono.ChronoLocalDateTime
import java.time.temporal.ChronoUnit
import java.util.Date
import kotlin.getValue
import kotlin.math.absoluteValue
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@Composable
fun TransactionsTable(transactions: List<Transaction>) {
    val listState = rememberLazyListState()

    LaunchedEffect(transactions) {
        if (transactions.isNotEmpty()) {
            listState.animateScrollToItem(0)
        }
    }

        val thisWeek = transactions.filter { tr -> tr.date.atZone(ZoneId.systemDefault()).isAfter(
            LocalDateTime.now().atZone(ZoneId.systemDefault()).minusDays(7).truncatedTo(
                ChronoUnit.DAYS)) }
        val thisMonth = transactions.filter { tr -> (tr.date.atZone(ZoneId.systemDefault()).isAfter(
            LocalDateTime.now().atZone(ZoneId.systemDefault()).minusDays(31)) && tr !in thisWeek) }
        val other = transactions.filter { tr -> (tr !in thisMonth) && (tr !in thisWeek)  }

        LazyColumn(
            state = listState,
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(0.dp),
            modifier = Modifier.padding(0.dp)
        ) {
            if (thisWeek.isNotEmpty())
                item(key = -1) {Title("На этой неделе")}
            items(items = thisWeek.sortedByDescending { it.date.toEpochMilli() }.mapIndexed{ ind, tr -> Pair(ind, tr) },
                key = { it.second.id }
            ) { ts ->
                val pos = if (thisWeek.size == 1) 2
                        else if (ts.first == 0) -1
                        else if (ts.first < thisWeek.size - 1) 0
                        else 1
                TransactionRow(pos = pos, transaction = ts.second)
            }
            if (thisMonth.isNotEmpty())
                item (key = -2) {Title("В этом месяце")}
            items(items = thisMonth.sortedByDescending { it.date.toEpochMilli() }.mapIndexed{ ind, tr -> Pair(ind, tr) },
                key = { it.second.id }
            ) { ts ->
                val pos = if (thisMonth.size == 1) 2
                        else if (ts.first == 0) -1
                        else if (ts.first < thisMonth.size - 1) 0
                         else 1
                TransactionRow(pos = pos, transaction = ts.second)
            }

            if (other.isNotEmpty())
                item (-3) {Title("В этом году")}
            items(items = other.sortedByDescending { it.date.toEpochMilli() }.mapIndexed{ ind, tr -> Pair(ind, tr) },
                key = { it.second.id }
            ) { ts ->
                val pos = if (other.size == 1) 2
                    else if (ts.first == 0) -1
                            else if (ts.first < other.size - 1) 0
                            else 1
                TransactionRow(pos = pos, transaction = ts.second)
            }
        }


}

@Composable
fun TransactionRow(pos: Int, transaction: Transaction) {
    val shape = if (pos == 0) RectangleShape
        else if (pos == -1) RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
        else if (pos == 1) RoundedCornerShape(bottomEnd = 12.dp, bottomStart = 12.dp)
        else RoundedCornerShape(12.dp)
    Surface(
        modifier = Modifier.height(50.dp),
        shape = shape,
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

@Composable
fun Title(name: String) {
        Box (
            contentAlignment = Alignment.BottomStart,
            modifier = Modifier
                .height(55.dp)
                .fillMaxWidth()
        ) {
            Text(
                name,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 10.dp, start = 10.dp)
            )
        }
}