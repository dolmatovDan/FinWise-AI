package com.spbsu_team7.finwise.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.spbsu_team7.finwise.app.ui.MainScreen
import com.spbsu_team7.finwise.app.ui.auth.AuthScreen
import com.spbsu_team7.finwise.app.ui.theme.FinanceTheme
import com.spbsu_team7.finwise.core.auth.TokenManager
import com.spbsu_team7.finwise.core.session.SessionManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity (): ComponentActivity() {
    @Inject lateinit var navFactory: NavigationActionsFactory
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FinanceTheme {
                Navigation( navFactory)
            }
        }
    }
}