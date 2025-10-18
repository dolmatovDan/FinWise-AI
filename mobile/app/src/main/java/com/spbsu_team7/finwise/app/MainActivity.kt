package com.spbsu_team7.finwise.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import com.spbsu_team7.finwise.app.ui.MainScreen
import com.spbsu_team7.finwise.app.ui.theme.FinanceTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val viewModel: ViewModel by viewModels()
            FinanceTheme {
                MainScreen(viewModel, viewModel.getEvents())
            }
        }
    }
}