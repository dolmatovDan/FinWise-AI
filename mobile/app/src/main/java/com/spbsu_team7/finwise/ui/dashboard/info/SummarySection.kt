package com.spbsu_team7.finwise.ui.dashboard.info

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.spbsu_team7.finwise.ui.theme.ExpenseRed
import com.spbsu_team7.finwise.ui.theme.IncomeGreen

@Composable
fun SummarySection(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SummaryCard(title = "Доход", amount = "20 000 ₽", color = IncomeGreen,  modifier = Modifier.weight(1f))
        SummaryCard(title = "Расход", amount = "18 000 ₽", color = ExpenseRed, modifier = Modifier.weight(1f))
        SummaryCard(title = "Баланс", amount = "2 000 ₽", color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.weight(1f))
    }
}

@Composable
fun SummaryCard(title: String, amount: String, color: Color, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.border(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outline,
            shape = MaterialTheme.shapes.medium
        ),
        shape = MaterialTheme.shapes.medium,
        contentColor = MaterialTheme.colorScheme.onSurface
    ) {
        Column(modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
        ) {
            Text(text = title, style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = amount, color = color, style = MaterialTheme.typography.displayLarge.copy(fontSize = 20.sp))
        }
    }
}
