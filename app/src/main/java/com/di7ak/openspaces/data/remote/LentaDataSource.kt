package com.di7ak.openspaces.data.remote

import com.di7ak.openspaces.data.Session
import javax.inject.Inject


class LentaDataSource @Inject constructor(
    private val lentaService: LentaService,
    private val session: Session
): BaseDataSource() {

    suspend fun fetch() = getResult {
        lentaService.fetch(
            sid = session.current?.sid ?: "")
    }
}