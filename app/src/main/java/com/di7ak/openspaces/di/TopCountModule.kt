package com.di7ak.openspaces.di

import com.di7ak.openspaces.data.Session
import com.di7ak.openspaces.data.local.AppDatabase
import com.di7ak.openspaces.data.local.TopCountDao
import com.di7ak.openspaces.data.remote.TopCountDataSource
import com.di7ak.openspaces.data.remote.TopCountService
import com.di7ak.openspaces.data.repository.TopCountRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import retrofit2.Retrofit
import javax.inject.Singleton


@Module
@InstallIn(ApplicationComponent::class)
class TopCountModule {
    @Provides
    fun provideTopCountService(retrofit: Retrofit): TopCountService =
        retrofit.create(TopCountService::class.java)

    @Singleton
    @Provides
    fun provideTopCountDataSource(topCountService: TopCountService) = TopCountDataSource(topCountService)

    @Singleton
    @Provides
    fun provideTopCountRepository(
        topCountDataSource: TopCountDataSource,
        topCountDao: TopCountDao,
        session: Session
    ) =
        TopCountRepository(topCountDataSource, topCountDao, session)

    @Singleton
    @Provides
    fun provideTopCountDao(db: AppDatabase) = db.topCountDao()
}