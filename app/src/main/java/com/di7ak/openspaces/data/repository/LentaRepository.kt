package com.di7ak.openspaces.data.repository

import com.di7ak.openspaces.data.Session
import com.di7ak.openspaces.data.local.AttachmentsDao
import com.di7ak.openspaces.data.local.LentaDao
import com.di7ak.openspaces.data.remote.LentaDataSource
import com.di7ak.openspaces.utils.performGetOperation
import javax.inject.Inject

class LentaRepository @Inject constructor(
    private val remoteDataSource: LentaDataSource,
    private val lentaDao: LentaDao,
    private val attachmentsDao: AttachmentsDao,
    private val session: Session
) {

    fun fetch() = performGetOperation(
        databaseQuery = {
            lentaDao.getEvents(session.current?.userId ?: 0).apply {
                forEach {
                    it.attachments = attachmentsDao.getAttachments(it.id)
                }
            }
        },
        networkCall = {
            remoteDataSource.fetch(session.current?.sid ?: "")
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