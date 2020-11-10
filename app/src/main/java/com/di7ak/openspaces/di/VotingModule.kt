package com.di7ak.openspaces.di

import com.di7ak.openspaces.data.Session
import com.di7ak.openspaces.data.remote.VoteDataSource
import com.di7ak.openspaces.data.remote.VoteService
import com.di7ak.openspaces.data.repository.VoteRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
class VotingModule {

    @Singleton
    @Provides
    fun provideVoteDataSource(voteService: VoteService) = VoteDataSource(voteService)

    @Provides
    fun provideVoteService(retrofit: Retrofit): VoteService =
        retrofit.create(VoteService::class.java)

    @Singleton
    @Provides
    fun provideVoteRepository(
        remoteDataSource: VoteDataSource,
        session: Session
    ) =
        VoteRepository(remoteDataSource, session)

}