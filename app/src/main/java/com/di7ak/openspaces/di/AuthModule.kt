package com.di7ak.openspaces.di

import com.di7ak.openspaces.data.local.AppDatabase
import com.di7ak.openspaces.data.local.AuthDao
import com.di7ak.openspaces.data.remote.AuthDataSource
import com.di7ak.openspaces.data.remote.AuthService
import com.di7ak.openspaces.data.repository.AuthRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
class AuthModule {
    @Singleton
    @Provides
    fun provideAuthDao(db: AppDatabase) = db.authDao()

    @Singleton
    @Provides
    fun provideAuthRepository(
        remoteDataSource: AuthDataSource,
        localDataSource: AuthDao
    ) = AuthRepository(remoteDataSource, localDataSource)

    @Provides
    fun provideAuthService(retrofit: Retrofit): AuthService =
        retrofit.create(AuthService::class.java)

    @Singleton
    @Provides
    fun provideAuthDataSource(authService: AuthService) = AuthDataSource(authService)
}