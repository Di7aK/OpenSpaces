package com.di7ak.openspaces.data.remote

import javax.inject.Inject


class ProfileDataSource @Inject constructor(
    private val profileService: ProfileService
): BaseDataSource() {

    suspend fun fetch(userId: String, sid: String) = getResult {
        profileService.fetch(userId = userId, sid = sid)
    }
}