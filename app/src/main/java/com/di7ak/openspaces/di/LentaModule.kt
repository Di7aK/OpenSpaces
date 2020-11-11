package com.di7ak.openspaces.di

import com.di7ak.openspaces.data.Session
import com.di7ak.openspaces.data.local.AppDatabase
import com.di7ak.openspaces.data.local.LentaDao
import com.di7ak.openspaces.data.remote.LentaDataSource
import com.di7ak.openspaces.data.remote.LentaService
import com.di7ak.openspaces.data.repository.LentaRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
class LentaModule {
    @Provides
    fun provideLentaService(retrofit: Retrofit): LentaService =
        retrofit.create(LentaService::class.java)

    @Singleton
    @Provides
    fun provideLentaDataSource(lentaService: LentaService) = LentaDataSource(lentaService)

    @Singleton
    @Provides
    fun provideLentaRepository(
        remoteDataSource: LentaDataSource,
        dao: LentaDao,
        session: Session
    ) =
        LentaRepository(remoteDataSource, dao, session)

    @Singleton
    @Provides
    fun provideLentaDao(db: AppDatabase) = db.lentaDao()
}