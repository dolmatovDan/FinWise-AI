package com.spbsu_team7.finwise.app.ui.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.spbsu_team7.finwise.app.Events
import com.spbsu_team7.finwise.app.UiState
import com.spbsu_team7.finwise.app.ui.dashboard.graphics.ChartsSection
import com.spbsu_team7.finwise.app.ui.dashboard.info.SummarySection
import com.spbsu_team7.finwise.app.ui.special.ErrorScreen
import com.spbsu_team7.finwise.app.ui.special.LoadingScreen

@Composable
fun DashboardScreen(
    uiState: UiState,
    events: Events,
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        when (uiState) {
            is UiState.Success -> {
                Column(
                    modifier = Modifier
                        .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 5.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {

                    SummarySection(uiState.status, modifier = Modifier.fillMaxWidth())
                    ChartsSection()
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
