package com.spbsu_team7.finwise.app.ui.dashboard.graphics

import android.util.Log
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
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
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.spbsu_team7.finwise.app.UiState
import com.spbsu_team7.finwise.app.ui.theme.ExpenseRed
import com.spbsu_team7.finwise.app.ui.theme.IncomeGreen


import com.spbsu_team7.finwise.core.model.Category
import com.spbsu_team7.finwise.core.model.ChartsData
import ir.ehsannarmani.compose_charts.LineChart
import ir.ehsannarmani.compose_charts.PieChart
import ir.ehsannarmani.compose_charts.models.HorizontalIndicatorProperties
import ir.ehsannarmani.compose_charts.models.LabelProperties
import ir.ehsannarmani.compose_charts.models.Line
import ir.ehsannarmani.compose_charts.models.Pie
import java.time.ZoneId

@Composable
fun ChartsSection(uiState: UiState.Success) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

        Surface(
            modifier = Modifier.fillMaxWidth().border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline,
                shape = MaterialTheme.shapes.medium
            ).weight(0.4f),
            shape = MaterialTheme.shapes.medium,
            contentColor = MaterialTheme.colorScheme.onSurface
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Расходы по категориям", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(12.dp))
                val transactions = uiState.chartsData.lastSixMonthTransactions
                IncomeExpensePieChart(uiState.chartsData.categoriesExpense)
            }
        }

        Surface(
            modifier = Modifier.fillMaxWidth().border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline,
                shape = MaterialTheme.shapes.medium
            ).weight(0.6f),
            shape = MaterialTheme.shapes.medium,
            contentColor = MaterialTheme.colorScheme.onSurface
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                Text(
                    "Доход и расход по дням",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(0.1f)
                )
//                val transactions = uiState.chartsData.lastSixMonthTransactions
//                IncomeExpenseLineChart(transactions.first,
//                    transactions.second)
                var selectedFilter by remember { mutableStateOf(FilterType.Month) }

                val transactions = uiState.transactions.sortedBy { it.date.toEpochMilli() }
                val incomeByDay = transactions.filter { it.amount >= 0 }.associate {
                    it.date.atZone(ZoneId.systemDefault()).dayOfMonth to it.amount
                }
                val incomeData = List(31) { incomeByDay.getOrDefault(it, 0) }
                val expenseByDay = transactions.filter { it.amount < 0 }.associate {
                    it.date.atZone(ZoneId.systemDefault()).dayOfMonth to -it.amount
                }
                val expenseData = List(31) { expenseByDay.getOrDefault(it, 0) }
                IncomeExpenseLineChart(
                    Modifier.weight(0.8f),
                    incomeData.runningReduce { acc, value -> acc + value },
                    expenseData.runningReduce { acc, value -> acc + value }
                )
                Filters(
                    modifier = Modifier.weight(0.1f),
                    onChange = { selectedFilter = it },
                    selectedFilter = selectedFilter
                )
            }
        }
    }
}

@Composable
fun Filters(
    modifier: Modifier,
    onChange: (FilterType) -> Unit,
    selectedFilter: FilterType
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        FilterType.entries.forEach {
                type ->
            val color = if (selectedFilter == type) MaterialTheme.colorScheme.surface
            else MaterialTheme.colorScheme.secondaryContainer
            Surface(
                modifier = Modifier.fillMaxSize().weight(1f).border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outline,
                    shape = MaterialTheme.shapes.medium
                )
                    .padding(0.dp)
                    .clickable(
                        onClick = { onChange(type) },
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ),
                color = color,
                shape = MaterialTheme.shapes.medium
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = type.text,
                        maxLines = 1,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun IncomeExpenseLineChart(
    modifier: Modifier,
    incomeData: List<Int>,
    expenseData: List<Int>
) {
    LineChart(
        modifier = modifier,
        data = remember {
            listOf(
                Line(
                    label = "Доход",
                    values = incomeData.map { it.toDouble() },
                    color = SolidColor(IncomeGreen)
            ),
                Line(
                    label = "Расход",
                    values = expenseData.map { it.toDouble() },
                    color = SolidColor(ExpenseRed)
                ),
            )
        },
        indicatorProperties = HorizontalIndicatorProperties(
            enabled = true,
            contentBuilder = { indicator ->
                indicator.toInt().toString()
            }
        ),
        labelProperties = LabelProperties(
            enabled = true,
            labels = listOf("1", "5", "10", "15", "20", "25", "30"),
            rotation = LabelProperties.Rotation(degree = 0f)
        ),
        curvedEdges = false,
        minValue = 0.0,
        maxValue = maxOf(incomeData.max(), expenseData.max()).toDouble() * 1.2
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
    Column (
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
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
                Modifier.fillMaxSize()
                    .padding(horizontal = 35.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Card(
                    colors = CardColors(selectedPie.color, selectedPie.color, selectedPie.color, selectedPie.color),
                    modifier = Modifier.fillMaxSize(0.05f).aspectRatio(1f)
                ){}
                Spacer(modifier = Modifier.width(5.dp))
                Text("${selectedPie.label!!}: ${selectedPie.data.toInt()} ₽")
            }

        }
    }

}

enum class FilterType(val text: String) {
    Month("за месяц"),
    Month3("за 3 месяца"),
    Year("за год")
}