package com.spbsu_team7.finwise.ui.transactions.transaction

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AddTransactionSection() {
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
            Text("Добавить операцию", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(value = "10/10/2025", onValueChange = {}, label = { Text("Дата") }, modifier = Modifier.weight(1f))
                OutlinedTextField(value = "0.00", onValueChange = {}, label = { Text("Стоимость") }, modifier = Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ExposedDropdownMenuBoxSample(modifier = Modifier.weight(1f))
                OutlinedTextField(value = "", onValueChange = {}, label = { Text("Комментарий") }, modifier = Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { /* placeholder */ },
                modifier = Modifier.border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outline,
                    shape = MaterialTheme.shapes.medium
                ).height(30.dp).fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                colors = ButtonColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    disabledContainerColor = MaterialTheme.colorScheme.surface,
                    disabledContentColor = MaterialTheme.colorScheme.surface,
                )
            )  {
                Text("Добавить")
            }
        }
    }
}

/**
 * Простая заглушка для дропдауна
 * (реальная логика не нужна — используем статический вид)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExposedDropdownMenuBoxSample(modifier: Modifier = Modifier) {
    // простой статичный OutlinedTextField с иконкой, не интерактивный
    OutlinedTextField(
        value = "Выбрать",
        onValueChange = {},
        label = { Text("Категории") },
        modifier = modifier,
        readOnly = true,
        trailingIcon = { /* иконка-дропдаун можно добавить */ }
    )
}
