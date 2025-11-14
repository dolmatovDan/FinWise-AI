package com.spbsu_team7.finwise.core.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf

class TestAuthRepository : AuthRepository {

    private val testAccessToken = "testAccess"
    private val testRefreshToken = "testRefresh"
    private val testEmail = "test@email"
    private val testPassword = "123"
    private var accessToken: String? = null

    private val _refreshToken = MutableStateFlow<String?>(null)
    private val refreshToken: StateFlow<String?> = _refreshToken

    override fun getAccessToken() = accessToken

    override fun getRefreshTokenStream(): StateFlow<String?> = refreshToken

    override fun login(email: String, password: String) {
        if (email == "test@email" && password == "123") {
            _refreshToken.value = testRefreshToken
            accessToken = testAccessToken
        }
    }

    override fun logout() {
        _refreshToken.value = null
        accessToken = null
    }

    override fun refreshToken() {

    }
}
