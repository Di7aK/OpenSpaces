package com.di7ak.openspaces.data.remote

import javax.inject.Inject

class VoteDataSource @Inject constructor(
    private val voteService: VoteService
): BaseDataSource() {

    suspend fun like(sid: String, ck: String, objectId: Int, objectType: Int, down: Int) = getResult {
        voteService.like(sid = sid, ck = ck, objectId = objectId, objectType = objectType, down = down)
    }
}