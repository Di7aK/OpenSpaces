package com.di7ak.openspaces.data.remote

import com.di7ak.openspaces.data.entities.lenta.LentaEntities
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface LentaService {

    @FormUrlEncoded
    @POST("lenta/")
    suspend fun fetch(
        @Field("sid") sid: String = "") : Response<LentaEntities>

}