package com.spbsu_team7.finwise.app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.automirrored.outlined.CompareArrows
import androidx.compose.material.icons.automirrored.outlined.Label
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.spbsu_team7.finwise.app.UiState
import com.spbsu_team7.finwise.app.ui.categories.CategoriesScreen
import com.spbsu_team7.finwise.app.ui.chatbot.ChatBotScreen
import com.spbsu_team7.finwise.app.ui.dashboard.DashboardScreen
import com.spbsu_team7.finwise.app.ui.navigation.NavItem
import com.spbsu_team7.finwise.app.ui.navigation.NavigationBar
import com.spbsu_team7.finwise.app.ui.transactions.TransactionsScreen
import kotlinx.coroutines.launch


@Composable
fun MainScreen(
    uiState: UiState
) {

    val pagerState = rememberPagerState(pageCount = { 4 })
    val coroutineScope = rememberCoroutineScope()

    val items = listOf(
        NavItem("Dashboard", Icons.Outlined.Dashboard),
        NavItem("Транзакции", Icons.AutoMirrored.Outlined.CompareArrows),
        NavItem("Категории", Icons.AutoMirrored.Outlined.Label),
        NavItem("AI Советы", Icons.Outlined.Lightbulb)
    )

    Scaffold(
        modifier = Modifier.padding(0.dp),
        bottomBar = { NavigationBar(items, selectedItem = pagerState.currentPage, onSelect =
            {
                ind -> coroutineScope.launch { pagerState.animateScrollToPage(ind) }
            }
        )
        }
    ) { paddingValues ->
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {page ->
            when (page) {
                0 -> DashboardScreen(uiState)
                1 -> TransactionsScreen(uiState)
                2 -> CategoriesScreen(uiState)
                3 -> ChatBotScreen(uiState)
            }
        }
    }
}
