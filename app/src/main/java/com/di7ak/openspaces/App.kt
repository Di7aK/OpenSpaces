package com.di7ak.openspaces

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : MultiDexApplication() {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)

        MultiDex.install(this)
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
    }
}