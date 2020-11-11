package com.di7ak.openspaces.data.remote

import javax.inject.Inject

class CommentsDataSource @Inject constructor(
    private val commentsService: CommentsService
): BaseDataSource() {

    suspend fun fetch(url: String, sid: String) = getResult {
        commentsService.fetch(url = url, sid = sid)
    }
}