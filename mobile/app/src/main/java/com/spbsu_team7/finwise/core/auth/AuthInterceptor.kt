package com.spbsu_team7.finwise.core.auth

import android.util.Log
import com.spbsu_team7.finwise.core.network.AuthApiService
import com.spbsu_team7.finwise.core.repository.AuthRepository
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class AuthInterceptor @Inject constructor(
    private val tokenManager: TokenManager
) : Interceptor
{
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
            .newBuilder()
            .header("Authorization", "${tokenManager.getAccessToken()}")
            .build()

        var response = chain.proceed(request)

        if (response.code() == 401) {
            response.close()
            tokenManager.refreshTokens()
            val newToken = tokenManager.getAccessToken()
            return if (newToken != null) {
                val newRequest = chain.request()
                    .newBuilder()
                    .header("Authorization", newToken)
                    .build()
                chain.proceed(newRequest)
            } else {
                response
            }
        }
        return response
    }
}