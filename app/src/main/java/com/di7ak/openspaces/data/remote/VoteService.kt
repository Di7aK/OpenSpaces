package com.di7ak.openspaces.data.remote

import com.di7ak.openspaces.data.entities.BaseEntity
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface VoteService {
    @FormUrlEncoded
    @POST("api/voting/like/")
    suspend fun like(
        @Field("sid") sid: String = "",
        @Field("CK") ck: String = "",
        @Field("Oid") objectId: Int = 0,
        @Field("Ot") objectType: Int = 0,
        @Field("Down") down: Int = 0
    ) : Response<BaseEntity>

    @FormUrlEncoded
    @POST("api/voting/delete")
    suspend fun unlike(
        @Field("sid") sid: String = "",
        @Field("CK") ck: String = "",
        @Field("Oid") objectId: Int = 0,
        @Field("Ot") objectType: Int = 0
    ) : Response<BaseEntity>

}