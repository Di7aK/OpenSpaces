package com.di7ak.openspaces.data.remote

import com.di7ak.openspaces.data.converters.MapperName
import com.di7ak.openspaces.data.entities.JournalEntity
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface JournalService {
    @MapperName("journal_mapper")
    @FormUrlEncoded
    @POST("api/journal/getRecords/")
    suspend fun fetch(
        @Field("sid") sid: String = "",
        @Field("L") limit: Int = 30,
        @Field("O") offset: Int = 0,
        @Field("S") filter: Int = 0,//0 - all, 2 - new, 1 - kolya???
        @Field("Android") android: Int = 1
    ) : Response<JournalEntity>
}