package com.di7ak.openspaces.ui.features.lenta

import com.di7ak.openspaces.data.entities.lenta2.Events
import com.di7ak.openspaces.data.repository.AssetsRepository
import com.di7ak.openspaces.utils.mapJsonTo
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.gson.Gson
import org.json.JSONObject

fun Events.toLentaModel(assetsRepository: AssetsRepository) : LentaModel {
    val config = Firebase.remoteConfig
    val mapperData = config.getString("lenta_mapper")
    //val mapperData = assetsRepository.openAsset("lenta.json")
    val json = Gson().toJson(this)
    val model = JSONObject(json).mapJsonTo(LentaModel::class.java, JSONObject(mapperData))
    return model
}