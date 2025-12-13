package com.spbsu_team7.finwise.core.repository

import android.util.Log
import com.spbsu_team7.finwise.core.auth.TokenManager
import com.spbsu_team7.finwise.core.network.AuthApiService
import com.spbsu_team7.finwise.core.network.LoginData
import com.spbsu_team7.finwise.core.repository.di.TestAppModule
import javax.inject.Inject

class ApiAuthRepository @Inject constructor(private val tokenManager: TokenManager, private val authService: AuthApiService) : AuthRepository {
    override suspend fun login(email: String, password: String): Boolean {
        val ans = authService.login(LoginData(email, password))
        if (ans.isSuccessful) {
            tokenManager.saveTokens(
                ans.body()?.accessToken ?: "",
                ans.body()?.refreshToken ?: ""
            )
            if (ans.body() == null) Log.e("auth", "auth body is null")
            else Log.d("auth", ans.body()!!.user.fullName)
            return true
        }
        Log.e("api", ans.message()?:"")
        return false
    }

    override fun logout() {
        tokenManager.clearTokens()
    }

    override fun refresh(refreshToken: String): String? {
        return ""
    }
}