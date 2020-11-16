package com.di7ak.openspaces.data.repository

import android.util.Log
import com.di7ak.openspaces.data.Session
import com.di7ak.openspaces.data.entities.LentaEntity
import com.di7ak.openspaces.data.local.AttachmentsDao
import com.di7ak.openspaces.data.local.LentaDao
import com.di7ak.openspaces.data.remote.LentaDataSource
import com.di7ak.openspaces.utils.Resource
import com.di7ak.openspaces.utils.performGetOperation
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LentaRepository @Inject constructor(
    private val remoteDataSource: LentaDataSource,
    private val lentaDao: LentaDao,
    private val attachmentsDao: AttachmentsDao,
    private val session: Session
) {

    fun fetch(page: Int) : Flow<Resource<LentaEntity>> {
        var nextPageCrutch = ""
        return performGetOperation(
            databaseQuery = {
                val items = lentaDao.getEvents(session.current?.userId ?: 0).apply {
                    forEach {
                        it.attachments = attachmentsDao.getAttachments(it.id)
                    }
                }
                LentaEntity(items = items, nextLinkUrl = nextPageCrutch)
            },
            networkCall = {
                remoteDataSource.fetch(session.current?.sid ?: "", page).apply {
                    nextPageCrutch = data?.nextLinkUrl ?: ""
                }
            },
            saveCallResult = {
                val signed = it.items.map { item ->
                    item.apply {
                        userId = session.current?.userId ?: 0
                        if (attachments.isNotEmpty()) attachmentsDao.insertAll(attachments)
                    }
                }
                lentaDao.insertAll(signed)
            }
        )
    }

    fun fetchNoStory(page: Int) = performGetOperation(
        networkCall = {
            remoteDataSource.fetch(session.current?.sid ?: "", page)
        },
        saveCallResult = {
            val signed = it.items.map { item ->
                item.apply {
                    userId = session.current?.userId ?: 0
                    if (attachments.isNotEmpty()) attachmentsDao.insertAll(attachments)
                }
            }
            lentaDao.insertAll(signed)
        }
    )
}