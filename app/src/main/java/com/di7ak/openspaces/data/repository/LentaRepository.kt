package com.di7ak.openspaces.data.repository

import com.di7ak.openspaces.data.remote.LentaDataSource
import com.di7ak.openspaces.utils.performGetOperation
import javax.inject.Inject


class LentaRepository @Inject constructor(
    private val remoteDataSource: LentaDataSource
) {

    fun fetch(sid: String) = performGetOperation(
        networkCall = { remoteDataSource.fetch(sid = sid) },
        saveCallResult = { }
    )

}