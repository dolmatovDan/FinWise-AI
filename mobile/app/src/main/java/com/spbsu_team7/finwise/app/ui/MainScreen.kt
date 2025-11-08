package com.spbsu_team7.finwise.app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.CompareArrows
import androidx.compose.material.icons.automirrored.filled.Label
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.automirrored.outlined.CompareArrows
import androidx.compose.material.icons.automirrored.outlined.Label
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.spbsu_team7.finwise.app.Events
import com.spbsu_team7.finwise.app.UiState
import com.spbsu_team7.finwise.app.ViewModel
import com.spbsu_team7.finwise.app.ui.categories.CategoriesScreen
import com.spbsu_team7.finwise.app.ui.chatbot.ChatBotScreen
import com.spbsu_team7.finwise.app.ui.dashboard.DashboardScreen
import com.spbsu_team7.finwise.app.ui.dashboard.DashboardViewModel
import com.spbsu_team7.finwise.app.ui.navigation.NavItem
import com.spbsu_team7.finwise.app.ui.navigation.NavigationBar
import com.spbsu_team7.finwise.app.ui.topbar.TopBar
import com.spbsu_team7.finwise.app.ui.transactions.TransactionsScreen
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


@Composable
fun MainScreen() {
    val pagerState = rememberPagerState(pageCount = { 4 })
    val coroutineScope = rememberCoroutineScope()

    val mainViewModel: ViewModel = hiltViewModel()
    val dashboardViewModel: DashboardViewModel = hiltViewModel()

    val items = listOf(
        NavItem("Dashboard", Icons.Outlined.Dashboard, Icons.Filled.Dashboard,),
        NavItem("Транзакции", Icons.AutoMirrored.Outlined.CompareArrows, Icons.AutoMirrored.Filled.CompareArrows),
        NavItem("Категории", Icons.AutoMirrored.Outlined.Label, Icons.AutoMirrored.Filled.Label),
        NavItem("AI Советы", Icons.Outlined.Lightbulb, Icons.Filled.Lightbulb)
    )

    Scaffold(
        modifier = Modifier.padding(0.dp),
        bottomBar = { NavigationBar(items, selectedItem = pagerState.currentPage, onSelect =
            {
                ind -> coroutineScope.launch { pagerState.animateScrollToPage(ind) }
            }
        )
        },
        topBar = { TopBar(mainViewModel.getState(), mainViewModel.getEvents()) }
    ) { paddingValues ->
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {page ->
            when (page) {
                0 -> DashboardScreen()
                1 -> TransactionsScreen(mainViewModel.getState(), mainViewModel.getEvents())
                2 -> CategoriesScreen(mainViewModel.getState(), mainViewModel.getEvents())
                3 -> ChatBotScreen(mainViewModel.getState(), mainViewModel.getEvents())
            }
        }
    }
}
