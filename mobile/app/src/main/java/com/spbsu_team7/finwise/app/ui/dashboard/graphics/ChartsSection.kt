package com.spbsu_team7.finwise.app.ui.dashboard.graphics

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import com.spbsu_team7.finwise.app.ui.theme.ExpenseRed
import com.spbsu_team7.finwise.app.ui.theme.IncomeGreen
import com.spbsu_team7.finwise.app.ui.transactions.transaction.AddTransactionSection
import com.spbsu_team7.finwise.app.ui.transactions.transaction.TransactionsTable

@Composable
fun ChartsSection() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

        // 1 PIE CHART
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
                Text("Расходы по категориям", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(12.dp))
                IncomeExpensePieChart()
            }
        }

        // 2 LINE CHART
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
                Text("Доход и расход по месяцам", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(12.dp))
                IncomeExpenseLineChart()
            }
        }
    }
}

@Composable
fun IncomeExpensePieChart() {
    val chartColors = listOf(IncomeGreen, ExpenseRed)

}

@Composable
fun IncomeExpenseLineChart() {
    // пример данных по месяцам
    val incomeData = listOf(1000f, 3000f, 2500f, 4000f, 3500f, 5000f)
    val expenseData = listOf(500f, 1200f, 1500f, 1700f, 1600f, 1900f)
    val months = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun")
}