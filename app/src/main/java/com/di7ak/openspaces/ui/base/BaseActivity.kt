package com.di7ak.openspaces.ui.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.di7ak.openspaces.utils.ThemeColors

open class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ThemeColors(this)
    }
}