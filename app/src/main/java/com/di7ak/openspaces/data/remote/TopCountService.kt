package com.di7ak.openspaces.data.remote

import com.di7ak.openspaces.data.converters.MapperName
import com.di7ak.openspaces.data.entities.TopCountEntity
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface TopCountService {
    @MapperName("top_count_mapper")
    @FormUrlEncoded
    @POST("api/common/getTopCounts/")
    suspend fun fetch(
        @Field("sid") sid: String = ""
    ) : Response<TopCountEntity>
}