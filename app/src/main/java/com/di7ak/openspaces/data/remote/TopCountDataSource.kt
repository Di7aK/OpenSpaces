package com.di7ak.openspaces.data.remote

import javax.inject.Inject


class TopCountDataSource @Inject constructor(
    private val topCountService: TopCountService
): BaseDataSource() {

    suspend fun fetch(sid: String) = getResult {
        topCountService.fetch(sid = sid)
    }
}