package com.spbsu_team7.finwise.ui.transactions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.spbsu_team7.finwise.ui.chatbot.guide.TipsSection
import com.spbsu_team7.finwise.ui.dashboard.graphics.ChartsSection
import com.spbsu_team7.finwise.ui.dashboard.info.SummarySection
import com.spbsu_team7.finwise.ui.dashboard.other.Export
import com.spbsu_team7.finwise.ui.dashboard.other.OpenSettings
import com.spbsu_team7.finwise.ui.transactions.transaction.AddTransactionSection
import com.spbsu_team7.finwise.ui.transactions.transaction.TransactionsTable

@Composable
fun TransactionsScreen() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 5.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            AddTransactionSection()
            TransactionsTable()
        }
    }
}