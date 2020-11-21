package com.di7ak.openspaces.di

import com.di7ak.openspaces.data.Session
import com.di7ak.openspaces.data.remote.JournalDataSource
import com.di7ak.openspaces.data.remote.JournalService
import com.di7ak.openspaces.data.repository.JournalRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
class JournalModule {
    @Provides
    fun provideJournalService(retrofit: Retrofit): JournalService =
        retrofit.create(JournalService::class.java)

    @Singleton
    @Provides
    fun provideJournalDataSource(journalService: JournalService) = JournalDataSource(journalService)

    @Singleton
    @Provides
    fun provideJournalRepository(
        journalDataSource: JournalDataSource,
        session: Session
    ) =
        JournalRepository(journalDataSource, session)
}