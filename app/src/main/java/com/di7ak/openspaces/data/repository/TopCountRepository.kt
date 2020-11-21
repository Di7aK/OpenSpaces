package com.di7ak.openspaces.data.repository

import com.di7ak.openspaces.data.CODE_SUCCESS
import com.di7ak.openspaces.data.Session
import com.di7ak.openspaces.data.entities.TopCountEntity
import com.di7ak.openspaces.data.local.TopCountDao
import com.di7ak.openspaces.data.remote.TopCountDataSource
import com.di7ak.openspaces.utils.Resource
import com.di7ak.openspaces.utils.performGetOperation
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class TopCountRepository @Inject constructor(
    private val remoteDataSource: TopCountDataSource,
    private val dao: TopCountDao,
    private val session: Session
) {
    fun fetch(): Flow<Resource<TopCountEntity?>> {
        var code = CODE_SUCCESS
        return performGetOperation(
            databaseQuery = {
                dao.getTopCount(session.current?.userId ?: 0)?.apply {
                    this.code = code
                }
            },
            networkCall = {
                remoteDataSource.fetch(
                    sid = session.current?.sid ?: ""
                ).apply {
                    code = this.data?.code ?: CODE_SUCCESS
                }
            },
            saveCallResult = {
                if (it.code == CODE_SUCCESS) {
                    it.userId = session.current?.userId ?: 0
                    dao.insert(it)
                }
            }
        )
    }

}