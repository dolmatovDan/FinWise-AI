package com.spbsu_team7.finwise.ui

import android.R
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.CompareArrows
import androidx.compose.material.icons.outlined.Label
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.ripple.createRippleModifierNode
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.Surface
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.spbsu_team7.finwise.ui.categories.CategoriesScreen
import com.spbsu_team7.finwise.ui.chatbot.ChatBotScreen
import com.spbsu_team7.finwise.ui.dashboard.DashboardScreen
import com.spbsu_team7.finwise.ui.navigation.NavItem
import com.spbsu_team7.finwise.ui.navigation.NavigationBar
import com.spbsu_team7.finwise.ui.navigation.NavigationChip
import com.spbsu_team7.finwise.ui.transactions.TransactionsScreen


@Composable
fun MainScreen() {
    var selectedItem by remember { mutableIntStateOf(0) }

    val items = listOf(
        NavItem("Dashboard", Icons.Outlined.Dashboard),
        NavItem("Транзакции", Icons.Outlined.CompareArrows),
        NavItem("Категории", Icons.Outlined.Label),
        NavItem("AI Советы", Icons.Outlined.Lightbulb)
    )

    Scaffold(
        modifier = Modifier.padding(0.dp),
        bottomBar = { NavigationBar(items, selectedItem = selectedItem, onSelect = { selectedItem = it }) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            when (selectedItem) {
                0 -> DashboardScreen()
                1 -> TransactionsScreen()
                2 -> CategoriesScreen()
                3 -> ChatBotScreen()
            }
        }
    }
}
