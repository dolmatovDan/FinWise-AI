package com.spbsu_team7.finwise.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.spbsu_team7.finwise.app.ui.MainScreen
import com.spbsu_team7.finwise.app.ui.auth.AuthScreen
import com.spbsu_team7.finwise.app.ui.auth.AuthUiState
import com.spbsu_team7.finwise.app.ui.auth.AuthViewModel
import com.spbsu_team7.finwise.app.ui.special.ErrorScreen
import com.spbsu_team7.finwise.app.ui.special.LoadingScreen


@Composable
fun Navigation(
    viewModel: AuthViewModel = hiltViewModel()
) {
    val uiState: AuthUiState by viewModel.uiState.collectAsStateWithLifecycle()
    when(uiState) {
        is AuthUiState.Authenticated -> MainScreen()
        is AuthUiState.Loading -> LoadingScreen()
        is AuthUiState.Error -> ErrorScreen((uiState as AuthUiState.Error).error)
        is AuthUiState.UnAuthenticated -> AuthScreen(
            (uiState as AuthUiState.UnAuthenticated).email,
            (uiState as AuthUiState.UnAuthenticated).password,
            viewModel::changeEmail,
            viewModel::changePassword,
            viewModel::login)
    }
}