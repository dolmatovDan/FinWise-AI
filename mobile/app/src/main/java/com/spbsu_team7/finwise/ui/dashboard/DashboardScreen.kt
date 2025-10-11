package com.spbsu_team7.finwise.ui.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.spbsu_team7.finwise.ui.dashboard.graphics.ChartsSection
import com.spbsu_team7.finwise.ui.chatbot.guide.TipsSection
import com.spbsu_team7.finwise.ui.dashboard.info.SummarySection
import com.spbsu_team7.finwise.ui.transactions.transaction.AddTransactionSection
import com.spbsu_team7.finwise.ui.transactions.transaction.TransactionsTable
import com.spbsu_team7.finwise.ui.dashboard.other.OpenSettings
import com.spbsu_team7.finwise.ui.dashboard.other.Export

@Composable
fun DashboardScreen() {
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
            SummarySection(modifier = Modifier.fillMaxWidth())
            AddTransactionSection()
            TransactionsTable()
            ChartsSection()
            Row (
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OpenSettings(Modifier.weight(1f))
                Export(Modifier.weight(1f))
            }
        }
    }
}
