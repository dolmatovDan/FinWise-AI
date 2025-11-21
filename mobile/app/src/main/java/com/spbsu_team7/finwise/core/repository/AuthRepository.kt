package com.spbsu_team7.finwise.core.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface AuthRepository {
    fun login(email: String, password: String): Boolean
    fun logout()
}