package com.spbsu_team7.finwise.app.ui.transactions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
                        .padding(start = 16.dp, end = 16.dp, top = 10.dp, bottom = 5.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    AddTransactionSection()
                    TransactionsTable(uiState.transactions)
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
