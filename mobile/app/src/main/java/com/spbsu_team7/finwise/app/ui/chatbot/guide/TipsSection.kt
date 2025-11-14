package com.spbsu_team7.finwise.app.ui.chatbot.guide

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.spbsu_team7.finwise.R
import com.spbsu_team7.finwise.app.UiState
import com.spbsu_team7.finwise.app.ui.theme.ExpenseRed
import com.spbsu_team7.finwise.app.ui.theme.HighPriority
import com.spbsu_team7.finwise.app.ui.theme.IncomeGreen
import com.spbsu_team7.finwise.app.ui.theme.MediumPriority
import com.spbsu_team7.finwise.app.ui.theme.SmallPriority
import com.spbsu_team7.finwise.app.ui.transactions.transaction.TransactionRow
import com.spbsu_team7.finwise.core.model.Advice
import com.spbsu_team7.finwise.core.model.Category
import kotlin.math.absoluteValue

@Composable
fun TipsSection(uiState: UiState.Success) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth().height(30.dp)
        ) {
            Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("AI рекомендации", style = MaterialTheme.typography.titleLarge)
        }

        LazyColumn(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            items(
                items = uiState.advices
            ) { adv ->
                AdviceRow(advice = adv)
            }
        }
    }
}

@Composable
fun AdviceRow(advice: Advice) {
    val textColor = if (advice.priority == 2) HighPriority
        else if (advice.priority == 1) MediumPriority
        else SmallPriority
    val text = if (advice.priority == 2) stringResource(R.string.high_priority)
        else if (advice.priority == 1) stringResource(R.string.medium_priority)
        else stringResource(R.string.small_priority)

    Surface(
        modifier = Modifier.border(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outline,
            shape = MaterialTheme.shapes.medium
        )
            .height(90.dp),
        shape = MaterialTheme.shapes.medium,
        color = textColor
    ) {
        Surface(
            modifier = Modifier
                .padding(start = 5.dp),
            shape = MaterialTheme.shapes.medium,
            contentColor = MaterialTheme.colorScheme.onSurface
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Icon(
                        imageVector = advice.icon,
                        contentDescription = advice.name,
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(vertical = 10.dp).weight(0.1f)
                    )
                    Column(
                        modifier = Modifier.weight(0.9f),
                        verticalArrangement = Arrangement.SpaceAround
                    ) {
                        Text (
                            text = advice.name,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Row (
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ){
                            Text(
                                text = text,
                                style = MaterialTheme.typography.labelSmall,
                                color = textColor
                            )
                            if (advice.economy > 0) {
                                Text(
                                    text = "экономия ${advice.economy}₽",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = IncomeGreen
                                )
                            }
                        }
                    }
                }
                Text(
                    modifier = Modifier.weight(1f),
                    text = advice.description,
                    style = MaterialTheme.typography.labelSmall,
                )
            }
        }
    }
}
