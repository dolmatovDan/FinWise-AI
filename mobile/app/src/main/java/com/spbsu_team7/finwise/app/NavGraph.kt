package com.spbsu_team7.finwise.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
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
import com.spbsu_team7.finwise.core.session.SessionManager
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject


@Composable
fun Navigation(
    navFactory: NavigationActionsFactory,
    startDestination: String = Destinations.AUTH_ROUTE,
    navController: NavHostController = rememberNavController(),
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    navActions: NavigationActions = remember(navController) {
        navFactory.create(navController, coroutineScope)
    }
) {
    NavHost(navController, startDestination = navActions.start()) {
        composable(Destinations.AUTH_ROUTE) {
            val viewModel: AuthViewModel = hiltViewModel()
            val uiState: AuthUiState by viewModel.uiState.collectAsStateWithLifecycle()
            AuthScreen(viewModel = viewModel) {
                navActions.navigateToUser(
                    (uiState as AuthUiState.Success).email,
                    (uiState as AuthUiState.Success).password)
            }
        }

        composable(Destinations.USER_ROUTE) {
            MainScreen (navActions::navigateToAuth)
        }
    }


}