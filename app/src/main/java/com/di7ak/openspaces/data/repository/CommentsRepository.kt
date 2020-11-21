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

    fun fetch(url: String) = performGetOperation(

        networkCall = {
            remoteDataSource.fetch(url, session.current?.sid ?: "")
        },
        saveCallResult = {}
    )

    fun add(postId: Int, type: Int, comment: String, cr: Int) = performGetOperation(
        networkCall = {
            val response = remoteDataSource.add(
                sid = session.current?.sid ?: "",
                type = type,
                id = postId,
                comment = comment,
                cr = cr,
                ck = session.current?.ck ?: ""
            )
            if (response.status == Resource.Status.SUCCESS) {
                val author = AuthorEntity(
                    session.current?.userId?.toLong() ?: 0,
                    session.current?.name ?: "",
                    session.current?.avatar ?: ""
                )
                Resource.success(
                    CommentItemEntity(
                        id = response.data?.id ?: 0,
                        author = author,
                        body = CommentParser.parse(response.data?.body ?: ""),
                        date = System.currentTimeMillis() / 1000,
                        likes = 0,
                        vote = 0,
                        dislikes = 0,
                        commentsCount = 0,
                        editLink = "kek",
                        parent = postId,
                        type = type,
                        attachments = listOf(),
                        userId = author.id?.toInt() ?: 0
                    )
                )
            } else response
        },
        saveCallResult = {
            commentsDao.insert(it)
        }
    )

    fun delete(type: Int, commentId: Int) = performGetOperation(
        networkCall = {
            val response = remoteDataSource.delete(
                sid = session.current?.sid ?: "",
                type = type,
                commentId = commentId,
                ck = session.current?.ck ?: ""
            )
            if (response.status == Resource.Status.SUCCESS) {
                commentsDao.delete(commentId)
            }
            response
        },
        saveCallResult = {}
    )

    fun edit(type: Int, commentId: Int, text: String) = performGetOperation(
        networkCall = {
            remoteDataSource.edit(
                sid = session.current?.sid ?: "",
                type = type,
                commentId = commentId,
                ck = session.current?.ck ?: "",
                text = text
            )
        },
        saveCallResult = {
            commentsDao.update(commentId, text)
        }
    )
}