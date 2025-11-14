package com.spbsu_team7.finwise.core.auth

import com.spbsu_team7.finwise.core.network.AuthApiService
import com.spbsu_team7.finwise.core.repository.AuthRepository
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class AuthInterceptor @Inject constructor(
    private val authRepository: AuthRepository
) : Interceptor
{
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
            .newBuilder()
            .header("Authorization", "${authRepository.getAccessToken()}")
            .build()

        var response = chain.proceed(request)

        if (response.code() == 401) {
            response.close()
            val newToken = authRepository.getRefreshTokenStream().value
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