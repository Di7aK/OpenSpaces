package com.di7ak.openspaces.di

import com.di7ak.openspaces.data.local.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
class AttachmentsModule {

    @Singleton
    @Provides
    fun provideAttachmentsDao(db: AppDatabase) = db.attachmentsDao()
}