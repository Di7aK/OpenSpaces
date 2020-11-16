package com.di7ak.openspaces.data.remote

import com.di7ak.openspaces.data.converters.MapperName
import com.di7ak.openspaces.data.entities.BaseEntity
import com.di7ak.openspaces.data.entities.CommentItemEntity
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

    @MapperName("comment_mapper")
    @FormUrlEncoded
    @POST("api/comments/add")
    suspend fun add(
        @Field("sid") sid: String = "",
        @Field("Type") type: Int = 0,
        @Field("Id") id: Int = 0,
        @Field("comment") comment: String = "",
        @Field("Cr") cr: Int = 0,
        @Field("CK") ck: String = ""
    ) : Response<CommentItemEntity>

    @MapperName("base_mapper")
    @FormUrlEncoded
    @POST("api/comments/delete")
    suspend fun delete(
        @Field("sid") sid: String = "",
        @Field("Type") type: Int = 0,
        @Field("Gcid") commentId: Int = 0,
        @Field("Dck") ck: String = ""
    ) : Response<BaseEntity>
}