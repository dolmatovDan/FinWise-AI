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
import com.spbsu_team7.finwise.app.ui.dashboard.DashboardUiState
import com.spbsu_team7.finwise.app.ui.dashboard.FilterTypes
import com.spbsu_team7.finwise.app.ui.theme.ExpenseRed
import com.spbsu_team7.finwise.app.ui.theme.IncomeGreen
import com.spbsu_team7.finwise.core.model.Async


import com.spbsu_team7.finwise.core.model.Category
import com.spbsu_team7.finwise.core.model.ChartsData
import com.spbsu_team7.finwise.core.model.Transaction
import com.spbsu_team7.finwise.core.repository.Stat
import ir.ehsannarmani.compose_charts.LineChart
import ir.ehsannarmani.compose_charts.PieChart
import ir.ehsannarmani.compose_charts.models.HorizontalIndicatorProperties
import ir.ehsannarmani.compose_charts.models.LabelProperties
import ir.ehsannarmani.compose_charts.models.Line
import ir.ehsannarmani.compose_charts.models.Pie
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date
import kotlin.time.Instant

@Composable
fun ChartsSection(transactions: Async<Stat> ,categoriesExpense: Async<Map<Category, Int>>, changeFilter: (FilterTypes) -> Unit, filter: FilterTypes) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

        Surface(
            modifier = Modifier.fillMaxWidth().weight(0.4f),
            shape = MaterialTheme.shapes.medium,
            contentColor = MaterialTheme.colorScheme.onSurface
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Расходы по категориям", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(12.dp))
                if (categoriesExpense is Async.Success) IncomeExpensePieChart(categoriesExpense.data)
            }
        }
        Surface(
            modifier = Modifier.fillMaxWidth().weight(0.6f),
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
                if (transactions is Async.Success) IncomeExpenseLineChart(
                        Modifier.weight(0.8f),
                        transactions.data
                    )
                Filters(
                    modifier = Modifier.weight(0.1f),
                    onChange = changeFilter,
                    selectedFilter = filter
                )
            }
        }
    }
}

@Composable
fun Filters(
    modifier: Modifier,
    onChange: (FilterTypes) -> Unit,
    selectedFilter: FilterTypes
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        FilterTypes.entries.forEach {
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
    transactions: Stat
) {

    LineChart(
        modifier = modifier,
        data =
            listOf(
                Line(
                    values = transactions.income.map { it.toDouble() },
                    color = SolidColor(IncomeGreen)
            ),
                Line(
                    values = transactions.expense.map { it.toDouble() },
                    color = SolidColor(ExpenseRed)
                ),
            )
        ,
        indicatorProperties = HorizontalIndicatorProperties(
            enabled = true,
            contentBuilder = { indicator ->
                indicator.toInt().toString()
            },
            textStyle = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurface)
        ),
//        labelProperties = LabelProperties(
//            enabled = true,
//            labels = listOf("1", "5", "10", "15", "20", "25", "30"),
//            rotation = LabelProperties.Rotation(degree = 0f),
//            textStyle = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurface)
//        ),
        curvedEdges = false,
        minValue = 0.0,
        maxValue = maxOf(transactions.income.maxOrNull() ?: 1, transactions.expense.maxOrNull() ?: 0).toDouble() * 1.2
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

