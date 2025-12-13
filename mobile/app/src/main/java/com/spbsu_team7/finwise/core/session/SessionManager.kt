package com.spbsu_team7.finwise.core.session

import android.util.Log
import androidx.compose.runtime.State
import com.spbsu_team7.finwise.app.ui.auth.AuthScreen
import com.spbsu_team7.finwise.core.auth.TokenManager
import com.spbsu_team7.finwise.core.repository.AuthRepository
import com.spbsu_team7.finwise.core.repository.Repository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

@Singleton
class SessionManager @Inject constructor(
    private val providerRepository: Provider<Repository>,
    private val tokenManager: TokenManager,
    private val authRepository: AuthRepository,
    private val coroutineScope: CoroutineScope
) {
    private var _sessionState: MutableStateFlow<SessionState> = MutableStateFlow(SessionState.AUTH)
    val sessionState: StateFlow<SessionState> = _sessionState
    private var currentRepository: Repository? = null

    init {
        tryLogin()
    }

    fun getAccessToken() =
        if (_sessionState.value != SessionState.AUTH) tokenManager.getAccessToken()
        else null

    fun refreshToken(): String? {
        val token: String? = authRepository.refresh(tokenManager.getRefreshToken()!!)

        return token?.also {
            tokenManager.saveTokens(token, tokenManager.getRefreshToken()!!)
        } ?: null.also { logout() }
    }

    fun logout() {
        _sessionState.value = SessionState.AUTH
        releaseRepository()
    }

    fun tryLogin() {
        if (tokenManager.isLogged()) {
            currentRepository = providerRepository.get()
            _sessionState.value = SessionState.USER
        }
    }

    fun login(email: String, password: String) {
        coroutineScope.launch {
            val res = authRepository.login(email, password)
            if (res) {
                currentRepository = providerRepository.get()
                _sessionState.value = SessionState.USER
            }
        }
    }

    fun getRepository(): Repository {
        return currentRepository!!
    }

    private fun releaseRepository() {
        currentRepository = null
    }
}