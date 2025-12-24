package com.spbsu_team7.finwise.release.di

import com.spbsu_team7.finwise.core.repository.ApiAuthRepository
import com.spbsu_team7.finwise.core.repository.AuthRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class TokenModule {
    @Singleton
    @Binds
    abstract fun bindAuthRepository(testAuthRepository: ApiAuthRepository): AuthRepository
}
