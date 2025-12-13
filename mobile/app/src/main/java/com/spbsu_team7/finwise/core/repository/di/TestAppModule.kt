package com.spbsu_team7.finwise.core.repository.di

import android.content.Context
import com.spbsu_team7.finwise.core.auth.AuthInterceptor
import com.spbsu_team7.finwise.core.auth.TokenManager
import com.spbsu_team7.finwise.core.network.ApiService
import com.spbsu_team7.finwise.core.network.AuthApiService
import com.spbsu_team7.finwise.core.repository.ApiAuthRepository
import com.spbsu_team7.finwise.core.repository.AuthRepository
import com.spbsu_team7.finwise.core.repository.Repository
import com.spbsu_team7.finwise.core.repository.TestAuthRepository
import com.spbsu_team7.finwise.core.repository.TestRepository
import com.spbsu_team7.finwise.core.session.SessionManager
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Provider
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TestAppModule {


//    @Provides
//    @Singleton
//    fun provideAuthRepository(
//    ): AuthRepository = TestAuthRepository()

private val _serverUrl = "http://10.159.181.110" //Debug ip

    @Provides
    @Singleton
    fun provideAuthApiService(): AuthApiService {
        val okHttpClient = OkHttpClient.Builder()
            .build()

        return Retrofit.Builder()
            .baseUrl("${_serverUrl}:8082")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AuthApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideApiService(authInterceptor: AuthInterceptor): ApiService {
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .build()

        return Retrofit.Builder()
            .baseUrl("${_serverUrl}:8080")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }


    @Provides
    @Singleton
    fun provideSessionManager(repositoryProvider: Provider<Repository>) = SessionManager(repositoryProvider)

    @Provides
    fun provideUserRepository(
    ): Repository = TestRepository(CoroutineScope(SupervisorJob() + Dispatchers.IO))

    @Provides
    @Singleton
    fun provideTokenManager(@ApplicationContext context: Context): TokenManager {
        return TokenManager(context)
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class TokenModule {
    @Singleton
    @Binds
    abstract fun bindAuthRepository(testAuthRepository: ApiAuthRepository): AuthRepository
}

//@Module
//@InstallIn(SingletonComponent::class)
//abstract class RepositoryModule {
//
////    @Singleton
////    @Binds
////    abstract fun bindTaskRepository(repository: TestRepository): Repository
//
//    @Singleton
//    @Binds
//    abstract fun bindAuthRepository(authRepository: TestAuthRepository): AuthRepository
//}

@Qualifier
@Retention()
annotation class ApplicationScope