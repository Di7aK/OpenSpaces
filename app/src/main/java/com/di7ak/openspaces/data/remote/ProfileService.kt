package com.di7ak.openspaces.data.remote

import com.di7ak.openspaces.data.converters.MapperName
import com.di7ak.openspaces.data.entities.ProfileEntity
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import retrofit2.http.Path


interface ProfileService {

    @MapperName("profile_mapper")
    @FormUrlEncoded
    @POST("ajax/mysite/index/{id}/")
    suspend fun fetch(
        @Field("sid") sid: String = "",
        @Path("id") userId: String = ""
    ) : Response<ProfileEntity>
}