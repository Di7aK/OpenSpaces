package com.di7ak.openspaces.data.repository

import com.di7ak.openspaces.data.Session
import com.di7ak.openspaces.data.remote.JournalDataSource
import com.di7ak.openspaces.utils.performGetOperation
import javax.inject.Inject

class JournalRepository @Inject constructor(
    private val remoteDataSource: JournalDataSource,
    private val session: Session
) {
    fun fetch(limit: Int, offset: Int, filter: Int) = performGetOperation(
        networkCall = {
            remoteDataSource.fetch(
                sid = session.current?.sid ?: "",
                limit = limit,
                offset = offset,
                filter = filter
            )
        },
        saveCallResult = {}
    )

}