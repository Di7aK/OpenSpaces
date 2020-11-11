package com.di7ak.openspaces.data.repository

import com.di7ak.openspaces.data.Session
import com.di7ak.openspaces.data.entities.LentaEntity
import com.di7ak.openspaces.data.local.LentaDao
import com.di7ak.openspaces.data.remote.LentaDataSource
import com.di7ak.openspaces.utils.performGetOperation
import javax.inject.Inject

class LentaRepository @Inject constructor(
    private val remoteDataSource: LentaDataSource,
    private val dao: LentaDao,
    private val session: Session
) {

    fun fetch() = performGetOperation(
        databaseQuery = {
            dao.getEvents(session.current?.userId ?: 0)
        },
        networkCall = {
            remoteDataSource.fetch(session.current?.sid ?: "")
        },
        saveCallResult = {
            val signed = it.items.map { item -> item.apply { userId = session.current?.userId ?: 0 } }
            dao.insertAll(signed)
        }
    )

}