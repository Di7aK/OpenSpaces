package com.di7ak.openspaces.data.repository

import com.di7ak.openspaces.data.Session
import com.di7ak.openspaces.data.remote.LentaDataSource
import com.di7ak.openspaces.utils.performGetOperation
import javax.inject.Inject

class LentaRepository @Inject constructor(
    private val remoteDataSource: LentaDataSource,
    private val session: Session
) {

    fun fetch() = performGetOperation(
        networkCall = {
            remoteDataSource.fetch(session.current?.sid ?: "")
        },
        saveCallResult = { }
    )

}