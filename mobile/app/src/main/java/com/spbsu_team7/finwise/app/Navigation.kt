package com.spbsu_team7.finwise.app

import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.spbsu_team7.finwise.app.Screens.AUTH_SCREEN
import com.spbsu_team7.finwise.app.Screens.USER_SCREEN
import com.spbsu_team7.finwise.core.session.SessionManager
import com.spbsu_team7.finwise.core.session.SessionState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton


private object Screens {
    const val AUTH_SCREEN = "auth"
    const val USER_SCREEN = "user"
}

object Destinations {
    const val AUTH_ROUTE = AUTH_SCREEN
    const val USER_ROUTE = USER_SCREEN
}


@Singleton
class NavigationActionsFactory @Inject constructor (private val sessionManager: SessionManager) {
    fun create(navController: NavHostController, coroutineScope: CoroutineScope ) = NavigationActions(sessionManager, navController, coroutineScope)
}
class NavigationActions (private val sessionManager: SessionManager, private val navController: NavHostController, coroutineScope: CoroutineScope ) {
    init {
        coroutineScope.launch {

            sessionManager.sessionState.drop(1).collect {
                if (it == SessionState.AUTH) {
                    navController.navigate(Destinations.AUTH_ROUTE) {
                        popUpTo(0)
                    }
                }
                else if (it == SessionState.USER) {
                    navController.navigate(Destinations.USER_ROUTE) {
                        popUpTo(0)
                    }
                }
            }
        }
    }

    fun start() = if (sessionManager.sessionState.value == SessionState.AUTH) Destinations.AUTH_ROUTE
                else Destinations.USER_ROUTE

    fun navigateToAuth() {
        sessionManager.logout()
    }

    fun navigateToUser(login: String, password: String) {
        sessionManager.login(
            login, password
        )
    }

}