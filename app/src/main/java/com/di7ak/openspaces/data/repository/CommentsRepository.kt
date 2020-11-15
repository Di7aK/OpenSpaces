package com.di7ak.openspaces.data.repository

import com.di7ak.openspaces.data.Session
import com.di7ak.openspaces.data.entities.AuthorEntity
import com.di7ak.openspaces.data.entities.CommentItemEntity
import com.di7ak.openspaces.data.local.AttachmentsDao
import com.di7ak.openspaces.data.local.CommentsDao
import com.di7ak.openspaces.data.remote.CommentsDataSource
import com.di7ak.openspaces.utils.CommentParser
import com.di7ak.openspaces.utils.Resource
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

    fun add(postId: Int, type: Int, comment: String, cr: Int) = performGetOperation(
        networkCall = {
            val response = remoteDataSource.add(
                session.current?.sid ?: "",
                type,
                postId,
                comment,
                cr,
                session.current?.ck ?: ""
            )
            if (response.status == Resource.Status.SUCCESS) {
                val author = AuthorEntity(
                    session.current?.userId?.toLong() ?: 0,
                    session.current?.name ?: "",
                    session.current?.avatar ?: ""
                )
                Resource.success(
                    CommentItemEntity(
                        response.data?.id ?: 0,
                        author,
                        CommentParser.parse(response.data?.body ?: ""),
                        System.currentTimeMillis() / 1000,
                        0,
                        0,
                        0,
                        0,
                        postId,
                        type,
                        listOf(),
                        author.id?.toInt() ?: 0
                    )
                )
            } else response
        },
        saveCallResult = {
            commentsDao.insert(it)
        }
    )
}