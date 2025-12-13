package com.spbsu_team7.finwise.core.network

import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import java.time.Instant

interface AuthApiService {
    @POST("/api/v1/auth/login")
    suspend fun login(@Body loginData: LoginData) : Response<LoginResult>
}

data class LoginData(val email: String, val password: String)

data class UserData(
    val id: Int,
    val email: String,
    @SerializedName("full_name") val fullName: String,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String
)

data class LoginResult(
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("refresh_token") val refreshToken: String,
    val user: UserData
)