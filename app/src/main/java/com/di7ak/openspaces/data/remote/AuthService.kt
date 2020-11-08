package com.di7ak.openspaces.data.remote

import com.di7ak.openspaces.data.entities.AuthEntity
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface AuthService {
    @FormUrlEncoded
    @POST("api/auth/")
    suspend fun login(
        @Field("login") login: String = "",
        @Field("password") password: String = "",
        @Field("code") code: String = "",
        @Field("method") method: String = "login") : Response<AuthEntity>

    @FormUrlEncoded
    @POST("api/auth/")
    suspend fun check(
        @Field("sid") sid: String = "",
        @Field("method") method: String = "check") : Response<AuthEntity>

}