package com.spbsu_team7.finwise.app.ui.dashboard.graphics

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp


import com.spbsu_team7.finwise.core.model.Category
import com.spbsu_team7.finwise.core.model.ChartsData
import ir.ehsannarmani.compose_charts.LineChart
import ir.ehsannarmani.compose_charts.PieChart
import ir.ehsannarmani.compose_charts.models.Line
import ir.ehsannarmani.compose_charts.models.Pie

@Composable
fun ChartsSection(chartsData: ChartsData) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

        Surface(
            modifier = Modifier.fillMaxWidth().border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline,
                shape = MaterialTheme.shapes.medium
            ),
            shape = MaterialTheme.shapes.medium,
            contentColor = MaterialTheme.colorScheme.onSurface
        ) {
            Column(modifier = Modifier.padding(16.dp).fillMaxHeight(fraction = 0.4f)) {
                Text("Расходы по категориям", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(12.dp))
                val transactions = chartsData.lastSixMonthTransactions
                IncomeExpensePieChart(chartsData.categoriesExpense)
            }
        }

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
                Text("Доход и расход по месяцам", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(12.dp))
                val transactions = chartsData.lastSixMonthTransactions
                IncomeExpenseLineChart(transactions.first,
                    transactions.second)
            }
        }
    }
}

@Composable
fun IncomeExpenseLineChart(incomeData: List<Int>, expenseData: List<Int>) {
    LineChart(
        data = remember {
            listOf(
                Line(
                    label = "Доход",
                    values = incomeData.map { it.toDouble() }.reversed(),
                    color = SolidColor(Color.Blue)
            ),
                Line(
                    label = "Расход",
                    values = expenseData.map { it.toDouble() }.reversed(),
                    color = SolidColor(Color.Red)
                ),
            )
        },
        minValue = 0.0,
        maxValue = maxOf(incomeData.max(), expenseData.max()).toDouble()
    )
}

@Composable
fun IncomeExpensePieChart(categoriesIncome: Map<Category, Int>) {
    var showLegend by remember {
        mutableStateOf(false)
    }
    var data by remember {
        mutableStateOf(
            categoriesIncome.map { Pie(label = it.key.name, data = it.value.toDouble(), color = it.key.color) }
        )
    }
    Column {
        PieChart(
            modifier = Modifier.fillMaxSize(fraction = 0.8f),
            data = data,
            onPieClick = {
                println("${it.label} Clicked")
                val pieIndex = data.indexOf(it)
                showLegend = !it.selected
                data = data.mapIndexed { mapIndex, pie -> pie.copy(selected = showLegend && (pieIndex == mapIndex)) }
            },
            selectedScale = 1.0f,
            scaleAnimEnterSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            ),
            colorAnimEnterSpec = tween(300),
            colorAnimExitSpec = tween(300),
            scaleAnimExitSpec = tween(300),
            spaceDegreeAnimExitSpec = tween(300),
            style = Pie.Style.Fill
        )
        if (showLegend) {
            val selectedPie = data.first { it.selected }
            Row(
                Modifier.fillMaxSize(0.8f)
                    .padding(horizontal = 35.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically) {
                Card(
                    colors = CardColors(selectedPie.color, selectedPie.color, selectedPie.color, selectedPie.color),
                    modifier = Modifier.fillMaxSize(0.05f).aspectRatio(1f)
                ){}
                Text(selectedPie.label!! + ": ")
                Text(selectedPie.data.toString())
            }

        }
    }

}