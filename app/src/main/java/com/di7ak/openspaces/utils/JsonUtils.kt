package com.di7ak.openspaces.utils

import android.util.Log
import org.json.JSONArray
import org.json.JSONObject
import java.lang.reflect.ParameterizedType
import java.util.regex.Pattern

fun <T> JSONObject.mapJsonTo(clazz: Class<T>, mapperData: JSONObject): T {
    val result = clazz.newInstance()

    clazz.declaredFields.forEach { field ->
        val name = field.name

        if (mapperData.has(name)) {
            val data = mapperData.getJSONObject(name)
            val type = data.getString("type")
            if (type == "value") {
                val paths = data.getJSONArray("paths")
                for (i in 0 until paths.length()) {
                    val path = paths.getString(i)
                    try {
                        val value = getValue(field.type, path)
                        field.isAccessible = true
                        field.set(result, value)
                        break
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Log.d("lol", e.toString())
                    }
                }
            } else if (type == "object") {
                val subMapperData = data.getJSONObject("map")
                val value = this.mapJsonTo(field.type, subMapperData)
                field.isAccessible = true
                field.set(result, value)
            } else if (type == "array") {
                try {
                    val paths = data.getJSONArray("paths")
                    val subMapperData = data.getJSONObject("map")
                    val genericType = (field.genericType as ParameterizedType).actualTypeArguments[0]
                    val target = getGenericList(genericType::class.java)
                    val genericClass = Class.forName(genericType.toString().substringAfter(" "))
                    for (i in 0 until paths.length()) {
                        val path = paths.getString(i)

                        val items = JSONArray(getValue(String::class.java, path))
                        for (j in 0 until items.length()) {
                            val item = items.getJSONObject(j)
                            val itemResult = item.mapJsonTo(
                                genericClass,
                                subMapperData
                            )
                            add(target, itemResult)
                        }
                    }
                    field.isAccessible = true
                    field.set(result, target)
                } catch (e: Exception) {
                    e.printStackTrace()
                    Log.d("lol", "parse", e)
                }
            }
        }
    }
    return result as T
}

fun <Type> JSONObject.getValue(type: Class<Type>, path: String): Type? {
    val jsonPath = PathParser(path).parse()
    var temp: JSONObject = this

    fun getTypedValue(type: Class<Type>, value: String) : Type? {
        return when {
            type.isAssignableFrom(Long::class.java) -> {
                value.toLong() as Type
            }
            type.isAssignableFrom(Boolean::class.java) -> {
                if(value.isInteger()) {
                    (value.toInt() == 1) as Type
                } else {
                    value.toBoolean() as Type
                }
            }
            type.isAssignableFrom(Int::class.java) -> {
                value.toInt() as Type
            }
            type.isAssignableFrom(String::class.java) -> {
                value as Type
            }
            else -> null
        }
    }

    jsonPath.forEach {
        when (it.type) {
            PathParser.PathType.OBJECT -> {
                temp = temp.getJSONObject(it.nodeName)
            }
            PathParser.PathType.VALUE -> {
                val value = temp.getString(it.nodeName)
                return getTypedValue(type,value)
            }
            PathParser.PathType.ARRAY_OBJECT -> {
                var array = temp.getJSONArray(it.nodeName)
                val indexes = it.indexes
                val last = indexes.removeLast()
                for(j in indexes.indices) {
                    array = array.getJSONArray(indexes[j])
                }
                temp = array.getJSONObject(last)
            }
            PathParser.PathType.ARRAY_VALUE -> {
                var array = temp.getJSONArray(it.nodeName)
                val last = it.indexes.removeLast()
                for(j in it.indexes.indices) {
                    array = array.getJSONArray(it.indexes[j])
                }
                val value = array.getString(last)
                return getTypedValue(type,value)
            }
        }
    }
    return null
}

private fun String.isInteger() = toIntOrNull()?.let { true } ?: false

private fun <Type> getGenericList(type: Class<Type>): MutableList<Type> {
    return mutableListOf()
}

private fun <Type> add(target: MutableList<Type>, value: Any) {
    target.add(value as Type)
}

class PathParser(path: String) {
    private val indexPattern = Pattern.compile("\\[([0-9]+)\\]")
    private val segments = path.split(".")
    private var current = 0

    fun parse(): List<Path> {
        val result = mutableListOf<Path>()
        while (hasNext()) result.add(next())
        return result
    }

    fun hasNext() = current < segments.size

    fun next(): Path {
        var type: PathType? = null
        val path = segments[current]
        current++

        val matcher = indexPattern.matcher(path)
        val indexes = mutableListOf<Int>()
        while (matcher.find()) {
            type = if(hasNext()) PathType.ARRAY_OBJECT
            else PathType.ARRAY_VALUE
            val index =  matcher.group(1)?.toIntOrNull() ?: 0
            indexes.add(index)
        }
        val nodeName = path.substringBefore("[")
        if(type == null) {
            type = if (hasNext()) PathType.OBJECT
            else PathType.VALUE
        }

        return Path(type, nodeName, indexes)
    }

    data class Path(
        val type: PathType = PathType.VALUE,
        val nodeName: String = "",
        val indexes: MutableList<Int> = mutableListOf()
    )

    enum class PathType {
        VALUE, ARRAY_OBJECT, ARRAY_VALUE, OBJECT
    }
}