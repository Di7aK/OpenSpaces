package com.di7ak.openspaces.data.repository

import com.di7ak.openspaces.data.Session
import com.di7ak.openspaces.data.remote.VoteDataSource
import com.di7ak.openspaces.utils.performGetOperation
import javax.inject.Inject

class VoteRepository @Inject constructor(
    private val remoteDataSource: VoteDataSource,
    private val session: Session
) {
    fun like(objectId: Int, objectType: Int, down: Boolean) = performGetOperation(
        networkCall = {
            remoteDataSource.like(
                sid = session.current?.sid ?: "",
                ck = session.current?.ck ?: "",
                objectId = objectId,
                objectType = objectType,
                down = if(down) 1 else 0
            )
        },
        saveCallResult = { }
    )
    fun unlike(objectId: Int, objectType: Int) = performGetOperation(
        networkCall = {
            remoteDataSource.unlike(
                sid = session.current?.sid ?: "",
                ck = session.current?.ck ?: "",
                objectId = objectId,
                objectType = objectType
            )
        },
        saveCallResult = { }
    )

}