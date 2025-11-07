package com.spbsu_team7.finwise.core.repository.di

import com.spbsu_team7.finwise.core.repository.Repository
import com.spbsu_team7.finwise.core.repository.TestRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TestAppModule {

    @Provides
    @Singleton
    @ApplicationScope
    fun provideApplicationScope() = CoroutineScope(SupervisorJob() + Dispatchers.IO)

//    @Provides
//    @Singleton
//    fun provideProductRepository(
//        @ApplicationScope applicationScope: CoroutineScope
//    ): Repository = TestRepository(applicationScope)
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Singleton
    @Binds
    abstract fun bindTaskRepository(repository: TestRepository): Repository
}

@Qualifier
@Retention()
annotation class ApplicationScope