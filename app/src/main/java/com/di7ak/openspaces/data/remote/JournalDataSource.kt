package com.di7ak.openspaces.data.remote

import javax.inject.Inject


class JournalDataSource @Inject constructor(
    private val journalService: JournalService
): BaseDataSource() {

    suspend fun fetch(sid: String, limit: Int, offset: Int, filter: Int) = getResult {
        journalService.fetch(sid = sid, limit = limit, offset = offset, filter = filter)
    }
}