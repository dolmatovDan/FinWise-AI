package com.spbsu_team7.finwise.app.ui.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.spbsu_team7.finwise.app.Events
import com.spbsu_team7.finwise.app.UiState
import com.spbsu_team7.finwise.app.ui.dashboard.graphics.ChartsSection
import com.spbsu_team7.finwise.app.ui.dashboard.info.SummarySection
import com.spbsu_team7.finwise.app.ui.special.ErrorScreen
import com.spbsu_team7.finwise.app.ui.special.LoadingScreen

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = hiltViewModel()
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
        when (uiState) {
            is DashboardUiState.Success -> {
                Column(
                    modifier = Modifier
                        .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 5.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {

                    SummarySection(uiState.status, modifier = Modifier.fillMaxWidth())
                    ChartsSection(uiState.chartsData, viewModel::changeFilter, uiState.filter)
                }
            }

            is DashboardUiState.Loading -> {
                LoadingScreen()
            }
            is DashboardUiState.Error -> {
                ErrorScreen(uiState.error)
            }
        }
    }
}
