package com.di7ak.openspaces.data.remote

import javax.inject.Inject

class CommentsDataSource @Inject constructor(
    private val commentsService: CommentsService
): BaseDataSource() {

    suspend fun fetch(url: String, sid: String) = getResult {
        commentsService.fetch(url = url, sid = sid)
    }

    suspend fun add(sid: String = "",
                    type: Int = 0,
                    id: Int = 0,
                    comment: String = "",
                    cr: Int = 0,
                    ck: String = "") = getResult {
        commentsService.add(sid = sid, type = type, id = id, cr = cr, comment = comment, ck = ck)
    }

    suspend fun delete(sid: String = "",
                    type: Int = 0,
                    commentId: Int = 0,
                    ck: String = "") = getResult {
        commentsService.delete(sid = sid, type = type, commentId = commentId, ck = ck)
    }
}