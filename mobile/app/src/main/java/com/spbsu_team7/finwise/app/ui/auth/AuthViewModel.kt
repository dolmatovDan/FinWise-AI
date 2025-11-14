package com.spbsu_team7.finwise.app.ui.auth


import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.spbsu_team7.finwise.app.ui.util.WhileUiSubscribed
import com.spbsu_team7.finwise.core.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject


sealed interface AuthUiState {
    data class UnAuthenticated(
        val email: String = "",
        val password: String = ""
    ) : AuthUiState

    object Authenticated : AuthUiState

    data class Error(
        val error: String
    ) : AuthUiState

    object Loading : AuthUiState
}

@HiltViewModel
class AuthViewModel @Inject constructor (
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _email = MutableStateFlow("")
    private val _password = MutableStateFlow("")

    val uiState =
        combine(authRepository.getRefreshTokenStream(), _email, _password) {
                refreshToken, email, password ->
            if (refreshToken == null)
                AuthUiState.UnAuthenticated(email, password)
            else
                AuthUiState.Authenticated
        }.stateIn(
            scope = viewModelScope,
            started = WhileUiSubscribed,
            initialValue = AuthUiState.Loading
        )

    fun changeEmail(email: String) {
        _email.value = email
    }

    fun changePassword(password: String) {
        _password.value = password
    }

    fun login() {
        authRepository.login(_email.value, _password.value)
    }

    fun logout() {
        authRepository.logout()
    }

    fun onRetry() {}
}