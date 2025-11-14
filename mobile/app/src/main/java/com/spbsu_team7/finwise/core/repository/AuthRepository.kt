package com.spbsu_team7.finwise.core.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface AuthRepository {
    fun getAccessToken(): String?
    fun getRefreshTokenStream(): StateFlow<String?>
    fun refreshToken()
    fun login(email: String, password: String)
    fun logout()
}