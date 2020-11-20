package com.di7ak.openspaces.data.repository

import com.di7ak.openspaces.data.Session
import com.di7ak.openspaces.data.remote.ProfileDataSource
import com.di7ak.openspaces.utils.performGetOperation
import javax.inject.Inject


class ProfileRepository @Inject constructor(
    private val remoteDataSource: ProfileDataSource,
    private val session: Session
) {
    fun fetch(userId: String) = performGetOperation(
        networkCall = {
            remoteDataSource.fetch(
                sid = session.current?.sid ?: "",
                userId = userId
            )
        },
        saveCallResult = {}
    )

}