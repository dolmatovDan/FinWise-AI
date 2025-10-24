package com.spbsu_team7.finwise.app.ui.categories.category

import androidx.activity.viewModels
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
import com.spbsu_team7.finwise.app.ViewModel
import com.spbsu_team7.finwise.core.model.Transaction

import com.spbsu_team7.finwise.app.ui.theme.ExpenseRed
import com.spbsu_team7.finwise.app.ui.theme.IncomeGreen
import com.spbsu_team7.finwise.core.model.Category
import kotlin.getValue
import kotlin.math.absoluteValue

@Composable
fun CategoriesTable(uiState: UiState.Success) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("Операции", style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(horizontal = 7.dp))
        LazyColumn(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            items(items = uiState.categories,
                key = { it.id }
            ) { cat ->
                CategoryRow(category = cat)
            }
        }
    }

}

@Composable
fun CategoryRow(category: Category) {
    Surface(
        modifier = Modifier.border(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outline,
            shape = MaterialTheme.shapes.medium
        ).height(40.dp),
        shape = MaterialTheme.shapes.medium,
        contentColor = MaterialTheme.colorScheme.onSurface
    ) {
        Row (
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        )
        {
            Icon (
                imageVector = category.icon,
                contentDescription = category.name,
                tint = category.color,
                modifier = Modifier.padding(vertical = 5.dp).weight(0.2f)
            )

            Text(text = category.name,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(0.8f)
            )
        }

    }
}