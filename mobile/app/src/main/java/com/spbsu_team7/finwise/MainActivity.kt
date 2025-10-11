package com.spbsu_team7.finwise

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.spbsu_team7.finwise.ui.MainScreen
import com.spbsu_team7.finwise.ui.theme.FinanceTheme
import com.spbsu_team7.finwise.ui.dashboard.DashboardScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FinanceTheme {
                MainScreen()
            }
        }
    }
}
