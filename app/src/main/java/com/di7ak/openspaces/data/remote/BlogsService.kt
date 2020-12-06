package com.di7ak.openspaces.data.remote

import com.di7ak.openspaces.data.converters.MapperName
import com.di7ak.openspaces.data.entities.LentaEntity
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import retrofit2.http.Path

interface BlogsService {

    @MapperName("blogs_mapper")
    @FormUrlEncoded
    @POST("/diary/view/user/{user}/all/p{page}/")
    suspend fun fetch(
        @Field("sid") sid: String = "",
        @Path("user") user: String = "",
        @Path("page") page: Int = 0
    ) : Response<LentaEntity>
}