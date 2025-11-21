package com.spbsu_team7.finwise.app.ui.dashboard.info

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.spbsu_team7.finwise.core.model.Status

import com.spbsu_team7.finwise.app.ui.theme.ExpenseRed
import com.spbsu_team7.finwise.app.ui.theme.IncomeGreen
import com.spbsu_team7.finwise.core.model.Async

@Composable
fun SummarySection(status: Async<Status>, modifier: Modifier = Modifier) {
    when(status) {
        is Async.Success -> SummarySectionContent(status.data, modifier)
        is Async.Loading -> SummarySectionContentLoading(modifier)
        is Async.Error -> SummarySectionContentLoading(modifier)
    }
}

@Composable
fun SummarySectionContent(status: Status, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SummaryCard(title = "Доход", amount = status.income.toString() + " ₽", color = IncomeGreen,  modifier = Modifier.weight(1f))
        SummaryCard(title = "Расход", amount = status.expence.toString() + " ₽", color = ExpenseRed, modifier = Modifier.weight(1f))
        SummaryCard(title = "Баланс", amount = status.balance.toString() + " ₽", color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.weight(1f))
    }
}

@Composable
fun SummarySectionContentLoading(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SummaryCard(title = "Доход", amount = "...", color = IncomeGreen,  modifier = Modifier.weight(1f))
        SummaryCard(title = "Расход", amount = "...", color = ExpenseRed, modifier = Modifier.weight(1f))
        SummaryCard(title = "Баланс", amount = "...", color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.weight(1f))
    }
}

@Composable
fun SummaryCard(title: String, amount: String, color: Color, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        contentColor = MaterialTheme.colorScheme.onSurface
    ) {
        Column(modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
        ) {
            Text(text = title, style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = amount, color = color, style = MaterialTheme.typography.displayLarge)
        }
    }
}
