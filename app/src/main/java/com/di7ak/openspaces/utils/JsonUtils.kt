package com.di7ak.openspaces.utils

import android.util.Log
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.jayway.jsonpath.Configuration
import com.jayway.jsonpath.JsonPath
import com.jayway.jsonpath.spi.json.GsonJsonProvider
import org.json.JSONArray
import org.json.JSONObject
import java.lang.reflect.ParameterizedType


fun <T> String.mapJsonTo(clazz: Class<T>, mapperData: JSONObject): T {
    val result = clazz.newInstance()
    val json = JsonPath.parse(this)
    clazz.declaredFields.forEach { field ->
        val name = field.name

        if (mapperData.has(name)) {
            val data = mapperData.getJSONObject(name)
            val type = data.getString("type")
            if (type == "value") {
                val paths = data.getJSONArray("paths")
                for (i in 0 until paths.length()) {
                    val path = paths.getString(i)
                    val jsonPath = JsonPath.compile(path)
                    try {
                        val value = json.read(jsonPath, field.type)
                        field.isAccessible = true
                        field.set(result, value)
                        break
                    } catch (e: Exception) {
                        e.printStackTrace()
                        //Log.d("lol", e.toString())
                    }
                }
            } else if (type == "object") {
                val subMapperData = data.getJSONObject("map")
                val value = this.mapJsonTo(field.type, subMapperData)
                field.isAccessible = true
                field.set(result, value)
            } else if (type == "array") {
                val paths = data.getJSONArray("paths")
                val subMapperData = data.getJSONObject("map")
                val genericType = (field.genericType as ParameterizedType).actualTypeArguments[0]
                val target = getGenericList(genericType::class.java)
                for (i in 0 until paths.length()) {
                    val path = paths.getString(i)

                    val conf = Configuration.builder().jsonProvider(GsonJsonProvider()).build()
                    val items = JsonPath.using(conf).parse(json).read<JsonArray>(path)
                    //val items: JSONArray = json.read(path)
                    for (j in 0 until paths.length()) {
                        val item = items[j]
                        Log.d("lol", item.toString())
                            val itemResult = item.toString().mapJsonTo(
                                genericType::class.java,
                                subMapperData
                            )
                        Log.d("lol", "" + itemResult.toString())
                            add(target, itemResult)
                    }
                }
                field.isAccessible = true
                field.set(result, target)
            }
        }
    }
    return result
}

private fun <Type> getGenericList(type: Class<Type>): MutableList<Type> {
    return mutableListOf()
}

private fun <Type> add(target: MutableList<Type>, value: Any) {
    target.add(value as Type)
}
/*
fun <T> String.mapJsonTo(clazz: Class<T>, mapperData: JSONObject): T {
    val result = clazz.newInstance()
    val json = JsonPath.parse(this)
    clazz.declaredFields.forEach { field ->
        val name = field.name

        if (mapperData.has(name)) {
            val gen = field.genericType.toString()

            if (gen.startsWith("class") && !gen.startsWith("class java.lang.")) {
                val subMapperData = mapperData.getJSONObject(name)
                val value = this.mapJsonTo(field.type, subMapperData)
                field.isAccessible = true
                field.set(result, value)
            } else if (gen.startsWith("java.util.List")) {
                val type = (field.genericType as ParameterizedType).actualTypeArguments[0]
                val list = field::class.java.newInstance()
                val array = json.read(field.type)
            } else {
                Log.d("lol", "${field.name} $gen")
                val paths = mapperData.getJSONArray(name)
                for (i in 0 until paths.length()) {
                    val path = paths.getString(i)
                    val jsonPath = JsonPath.compile(path)

                    try {
                        val value = json.read(jsonPath, field.type)
                        field.isAccessible = true
                        field.set(result, value)
                        break
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Log.d("lol", e.toString())
                    }
                }
            }
        }

    }
    return result
}*/

class test() {
    var id: Int = 0
}