package com.di7ak.openspaces.utils

import com.di7ak.openspaces.data.entities.AuthEntity
import com.di7ak.openspaces.data.entities.LentaEntity
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

internal class SpacesConverterFactory private constructor(
    private val remoteConfig: FirebaseRemoteConfig
) : Converter.Factory() {
    override fun responseBodyConverter(
        type: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit
    ): Converter<ResponseBody, *> {
        return SpacesResponseBodyConverter(remoteConfig, type)
    }

    private class SpacesResponseBodyConverter constructor(private val remoteConfig: FirebaseRemoteConfig, private val type: Type) :
        Converter<ResponseBody, Any?> {
        override fun convert(responseBody: ResponseBody): Any? {
            val typed = Class.forName(type.toString().substringAfter(" "))
            if(typed.isAssignableFrom(LentaEntity::class.java)) {
                val json = JSONObject(responseBody.string())
                val mapData = JSONObject(remoteConfig.getString("lenta_mapper"))
                return json.mapJsonTo(typed, mapData)
            } else if(typed.isAssignableFrom(AuthEntity::class.java)) {
                val json = JSONObject(responseBody.string())
                val mapData = JSONObject(remoteConfig.getString("auth_mapper"))
                val result = json.mapJsonTo(typed, mapData)
                return result
            }
            return null
        }
    }

    companion object {
        fun create(remoteConfig: FirebaseRemoteConfig): Converter.Factory {
            return SpacesConverterFactory(remoteConfig)
        }
    }
}