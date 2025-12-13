package com.spbsu_team7.finwise.core.repository

import com.spbsu_team7.finwise.core.auth.TokenManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class TestAuthRepository @Inject constructor(private val tokenManager: TokenManager) : AuthRepository  {

    private val testAccessToken = "testAccess"
    private val testRefreshToken = "testRefresh"
    private val testEmail = ""
    private val testPassword = "1"

    override suspend fun login(email: String, password: String): Boolean {
        return if (email == testEmail && password == testPassword) {
            tokenManager.saveTokens(testAccessToken, testRefreshToken)
            true
        } else false
    }

    override fun logout() {
        tokenManager.clearTokens()
    }

    override fun refresh(refreshToken: String): String? {
        return tokenManager.getAccessToken()
    }

}
