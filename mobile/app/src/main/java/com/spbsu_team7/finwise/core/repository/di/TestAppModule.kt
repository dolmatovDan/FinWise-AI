package com.spbsu_team7.finwise.core.repository.di

import com.spbsu_team7.finwise.core.auth.AuthInterceptor
import com.spbsu_team7.finwise.core.network.ApiService
import com.spbsu_team7.finwise.core.network.AuthApiService
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


    @Provides
    @Singleton
    fun provideAuthRepository(
    ): AuthRepository = TestAuthRepository()



    @Provides
    @Singleton
    fun provideAuthApiService(authInterceptor: AuthInterceptor): AuthApiService {
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .build()

        return Retrofit.Builder()
            .baseUrl("https://your-api.com/")
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
            .baseUrl("https://your-api.com/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideSessionManager(repositoryProvider: Provider<Repository>) = SessionManager(repositoryProvider)

    @Provides
    fun provideProductRepository(
    ): Repository = TestRepository(CoroutineScope(SupervisorJob() + Dispatchers.IO))

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