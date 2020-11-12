package com.di7ak.openspaces.data.remote

import com.di7ak.openspaces.data.converters.MapperName
import com.di7ak.openspaces.data.entities.CommentsEntity
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import retrofit2.http.Url

interface CommentsService {

    @MapperName("comments_mapper")
    @FormUrlEncoded
    @POST
    suspend fun fetch(
        @Url url: String,
        @Field("sid") sid: String = ""
    ) : Response<CommentsEntity>
}