package com.di7ak.openspaces.data.converters

import com.di7ak.openspaces.utils.mapJsonTo
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

internal class ConfigMapperConverterFactory private constructor(
    private val remoteConfig: FirebaseRemoteConfig
) : Converter.Factory() {
    override fun responseBodyConverter(
        type: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit
    ): Converter<ResponseBody, *> {
        return SpacesResponseBodyConverter(remoteConfig, type, annotations)
    }

    private class SpacesResponseBodyConverter constructor(
        private val remoteConfig: FirebaseRemoteConfig,
        private val type: Type,
        private val annotations: Array<Annotation>
    ) :
        Converter<ResponseBody, Any?> {
        override fun convert(responseBody: ResponseBody): Any? {
            val mapperName = annotations.find { it is MapperName } as MapperName?
            if (mapperName != null) {
                val typed = Class.forName(type.toString().substringAfter(" "))
                val name = mapperName.name
                val json = JSONObject(responseBody.string())
                val mapData = JSONObject(remoteConfig.getString(name))
                return json.mapJsonTo(typed, mapData)
            }
            return null
        }
    }

    companion object {
        fun create(remoteConfig: FirebaseRemoteConfig): Converter.Factory {
            return ConfigMapperConverterFactory(remoteConfig)
        }
    }
}