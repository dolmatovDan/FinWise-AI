package com.spbsu_team7.finwise.app.ui.transactions.transactionOptions

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.spbsu_team7.finwise.core.model.Category

@Composable
fun TransactionCategory(modifier: Modifier) {
    val categories = listOf("Еда", "Транспорт", "Развлечения", "Здоровье", "Одежда")
    var expanded by remember { mutableStateOf(false) }
    var selectedCategory: Category?  by remember { mutableStateOf(null) }

        TextWithOption(
            name = "Категория",
            modifier = modifier,
            value = selectedCategory?.name ?: "",
            placeholder = "Выберите категорию",
        )
}