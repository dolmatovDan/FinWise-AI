package com.spbsu_team7.finwise.core.auth

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import androidx.core.content.edit

@Singleton
class TokenManager @Inject constructor (
    @ApplicationContext private val context: Context
) {
    private val prefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    fun saveTokens(accessToken: String, refreshToken: String) {
        prefs.edit(commit = true) {
            putString("access_token", accessToken)
            putString("refresh_token", refreshToken)
        }
    }

    fun getAccessToken() = prefs.getString("access_token", null)
    fun getRefreshToken() = prefs.getString("refresh_token", null)


    fun clearTokens() {
        prefs.edit(commit = true) { clear() }
    }

    fun isLoggedIn(): Boolean = getAccessToken() != null
}