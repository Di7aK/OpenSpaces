package com.di7ak.openspaces.data.remote

import javax.inject.Inject

class BlogsDataSource @Inject constructor(
    private val service: BlogsService
): BaseDataSource() {

    suspend fun fetch(sid: String, user: String, page: Int) = getResult {
        service.fetch(sid = sid, user = user, page = page)
    }
}