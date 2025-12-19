package com.spbsu_team7.finwise.core.auth

import android.util.Log
import com.spbsu_team7.finwise.core.network.AuthApiService
import com.spbsu_team7.finwise.core.repository.AuthRepository
import com.spbsu_team7.finwise.core.session.SessionManager
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class AuthInterceptor @Inject constructor(
    private val sessionManager: SessionManager
) : Interceptor
{
    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request()
            .newBuilder()

        val request = requestBuilder
            .header("Authorization", "Bearer ${sessionManager.getAccessToken()}")
            .build()

        var response = chain.proceed(request)

        if (response.code() == 401) {
            response.close()
            val newToken = sessionManager.refreshToken()
            if (newToken != null) {
                val newRequest = requestBuilder
                    .header("Authorization", "Bearer $newToken")
                    .build()
                return chain.proceed(newRequest)
            }
        }
        return response
    }
}