package com.di7ak.openspaces.data.repository

import android.content.Context
import java.io.InputStreamReader

class AssetsRepository (
    private val context: Context
) {

    fun openAsset(name: String) : String {
        val stream = InputStreamReader(context.resources.assets.open(name))
        return stream.use { it.readText() }
    }
}