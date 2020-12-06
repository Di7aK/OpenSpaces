package com.di7ak.openspaces.di

import com.di7ak.openspaces.data.Session
import com.di7ak.openspaces.data.remote.BlogsDataSource
import com.di7ak.openspaces.data.remote.BlogsService
import com.di7ak.openspaces.data.repository.BlogsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
class BlogsModule {
    @Provides
    fun provideService(retrofit: Retrofit): BlogsService =
        retrofit.create(BlogsService::class.java)

    @Singleton
    @Provides
    fun provideDataSource(service: BlogsService) = BlogsDataSource(service)

    @Singleton
    @Provides
    fun provideRepository(
        remoteDataSource: BlogsDataSource,
        session: Session
    ) =
        BlogsRepository(remoteDataSource, session)
}