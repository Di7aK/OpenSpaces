package com.di7ak.openspaces.data.remote

import javax.inject.Inject


class LentaDataSource @Inject constructor(
    private val lentaService: LentaService
): BaseDataSource() {

    suspend fun fetch(sid: String) = getResult {
        lentaService.fetch(
            sid = sid)
    }
}