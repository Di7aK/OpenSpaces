package com.di7ak.openspaces.di

import com.di7ak.openspaces.data.Session
import com.di7ak.openspaces.data.remote.ProfileDataSource
import com.di7ak.openspaces.data.remote.ProfileService
import com.di7ak.openspaces.data.repository.ProfileRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import retrofit2.Retrofit
import javax.inject.Singleton


@Module
@InstallIn(ApplicationComponent::class)
class ProfileModule {
    @Provides
    fun provideProfileService(retrofit: Retrofit): ProfileService =
        retrofit.create(ProfileService::class.java)

    @Singleton
    @Provides
    fun provideProfileDataSource(profileService: ProfileService) = ProfileDataSource(profileService)

    @Singleton
    @Provides
    fun provideProfileRepository(
        profileDataSource: ProfileDataSource,
        session: Session
    ) =
        ProfileRepository(profileDataSource, session)
}