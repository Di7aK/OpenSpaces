package com.di7ak.openspaces.data.repository

import com.di7ak.openspaces.data.Session
import com.di7ak.openspaces.data.local.AttachmentsDao
import com.di7ak.openspaces.data.local.CommentsDao
import com.di7ak.openspaces.data.local.LentaDao
import com.di7ak.openspaces.data.remote.CommentsDataSource
import com.di7ak.openspaces.data.remote.LentaDataSource
import com.di7ak.openspaces.utils.performGetOperation
import javax.inject.Inject


class CommentsRepository @Inject constructor(
    private val remoteDataSource: CommentsDataSource,
    private val commentsDao: CommentsDao,
    private val attachmentsDao: AttachmentsDao,
    private val session: Session
) {

    fun fetch(postId: Int, url: String) = performGetOperation(
        databaseQuery = {
            commentsDao.getComments(postId).apply {
                forEach {
                    it.attachments = attachmentsDao.getAttachments(it.id)
                }
            }
        },
        networkCall = {
            remoteDataSource.fetch(url, session.current?.sid ?: "")
        },
        saveCallResult = {
            val signed = it.items.map { item ->
                item.apply {
                    parent = postId
                    if (attachments.isNotEmpty()) attachmentsDao.insertAll(attachments)
                }
            }
            commentsDao.insertAll(signed)
        }
    )
}