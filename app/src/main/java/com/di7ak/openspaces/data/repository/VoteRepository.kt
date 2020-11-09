package com.di7ak.openspaces.data.repository

import com.di7ak.openspaces.data.Session
import com.di7ak.openspaces.data.remote.VoteDataSource
import com.di7ak.openspaces.utils.performGetOperation
import javax.inject.Inject

class VoteRepository @Inject constructor(
    private val remoteDataSource: VoteDataSource,
    private val session: Session
) {
    fun like(objectId: Int, objectType: Int, down: Int) = performGetOperation(
        networkCall = {
            remoteDataSource.like(
                sid = session.current?.sid ?: "",
                ck = session.current?.ck ?: "",
                objectId = objectId,
                objectType = objectType,
                down = down
            )
        },
        saveCallResult = { }
    )

}