package com.spbsu_team7.finwise.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.spbsu_team7.finwise.app.ui.MainScreen
import com.spbsu_team7.finwise.app.ui.auth.AuthScreen
import com.spbsu_team7.finwise.app.ui.auth.AuthUiState
import com.spbsu_team7.finwise.app.ui.auth.AuthViewModel
import com.spbsu_team7.finwise.app.ui.special.ErrorScreen
import com.spbsu_team7.finwise.app.ui.special.LoadingScreen
import com.spbsu_team7.finwise.core.auth.TokenManager
import javax.inject.Inject


@Composable
fun Navigation(
) {


    val navController = rememberNavController()

    NavHost(navController, startDestination = "auth") {

        composable("auth") {
            val viewModel: AuthViewModel = hiltViewModel()
            AuthScreen(viewModel = viewModel) {
                if (viewModel.login())
                    navController.navigate("user") {
                        popUpTo("auth") { inclusive = true }
                    }
            }
        }

        composable("user") {
            MainScreen {
                navController.navigate("auth") {
                    popUpTo("user") { inclusive = true }
                }
            }
        }
    }
}