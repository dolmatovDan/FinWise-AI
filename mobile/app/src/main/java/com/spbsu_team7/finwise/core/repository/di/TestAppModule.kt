package com.spbsu_team7.finwise.core.repository.di

import com.spbsu_team7.finwise.core.repository.Repository
import com.spbsu_team7.finwise.core.repository.TestRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TestAppModule {

    @Provides
    @Singleton
    fun provideRepository(): Repository {
        return TestRepository()
    }
}