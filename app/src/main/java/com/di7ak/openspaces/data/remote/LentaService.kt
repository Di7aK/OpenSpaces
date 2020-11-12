package com.di7ak.openspaces.data.remote

import com.di7ak.openspaces.data.converters.MapperName
import com.di7ak.openspaces.data.entities.LentaEntity
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface LentaService {

    @MapperName("lenta_mapper")
    @FormUrlEncoded
    @POST("lenta/")
    suspend fun fetch(
        @Field("sid") sid: String = ""
    ) : Response<LentaEntity>
}