package com.spbsu_team7.finwise.app.ui.dashboard

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
import com.spbsu_team7.finwise.app.UiState
import com.spbsu_team7.finwise.app.ui.dashboard.graphics.ChartsSection
import com.spbsu_team7.finwise.app.ui.dashboard.info.SummarySection
import com.spbsu_team7.finwise.app.ui.dashboard.other.OpenSettings
import com.spbsu_team7.finwise.app.ui.dashboard.other.Export

@Composable
fun DashboardScreen(
    uiState: UiState
) {
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
            when (uiState) {
                is UiState.Success -> {
                    SummarySection(uiState.status, modifier = Modifier.fillMaxWidth())
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
    }
}
