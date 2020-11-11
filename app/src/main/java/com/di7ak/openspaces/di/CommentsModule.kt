package com.di7ak.openspaces.di

import com.di7ak.openspaces.data.Session
import com.di7ak.openspaces.data.local.AppDatabase
import com.di7ak.openspaces.data.local.AttachmentsDao
import com.di7ak.openspaces.data.local.CommentsDao
import com.di7ak.openspaces.data.remote.CommentsDataSource
import com.di7ak.openspaces.data.remote.CommentsService
import com.di7ak.openspaces.data.repository.CommentsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
class CommentsModule {
    @Provides
    fun provideCommentsService(retrofit: Retrofit): CommentsService =
        retrofit.create(CommentsService::class.java)

    @Singleton
    @Provides
    fun provideCommentsDataSource(commentsService: CommentsService) = CommentsDataSource(commentsService)

    @Singleton
    @Provides
    fun provideCommentsRepository(
        remoteDataSource: CommentsDataSource,
        lentaDao: CommentsDao,
        attachmentsDao: AttachmentsDao,
        session: Session
    ) =
        CommentsRepository(remoteDataSource, lentaDao, attachmentsDao, session)

    @Singleton
    @Provides
    fun provideCommentsDao(db: AppDatabase) = db.commentsDao()
}