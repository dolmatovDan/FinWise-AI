package com.spbsu_team7.finwise.app.ui.transactions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.FloatingActionButtonElevation
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import com.spbsu_team7.finwise.app.Events
import com.spbsu_team7.finwise.app.UiState
import com.spbsu_team7.finwise.app.ui.special.ErrorScreen
import com.spbsu_team7.finwise.app.ui.special.LoadingScreen

import com.spbsu_team7.finwise.app.ui.transactions.transaction.AddTransactionSection
import com.spbsu_team7.finwise.app.ui.transactions.transaction.TransactionsTable

@Composable
fun TransactionsScreen(uiState: UiState, events: Events) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        when (uiState) {
            is UiState.Success -> {
                Column(
                    modifier = Modifier
                        .padding(start = 16.dp, end = 16.dp, bottom = 5.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {

                    var expanded by remember { mutableStateOf(false) }
                    Box(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        TransactionsTable(uiState.transactions)
                        Button(
                            onClick = { expanded = true },
                            shape = CircleShape,
                            modifier = Modifier.align(Alignment.BottomEnd)
                                .padding(bottom = 10.dp)
                                .shadow(4.dp, shape = CircleShape)
                        ) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = "Добавить",
                                modifier = Modifier.size(30.dp),
                            )
                        }
                    }
                    AddTransactionSection(expanded, { expanded = !expanded },uiState.categories, events.sendTransaction)
                }
            }
            is UiState.Loading -> {
                LoadingScreen()
            }
            is UiState.Error -> {
                ErrorScreen(uiState.error)
                events.onRetry()
            }
        }
    }
}
