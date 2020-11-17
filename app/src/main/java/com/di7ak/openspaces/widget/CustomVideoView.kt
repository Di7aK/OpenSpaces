package com.di7ak.openspaces.widget

import android.content.Context
import android.util.AttributeSet
import com.potyvideo.library.AndExoPlayerView

class CustomVideoView(context: Context, attributeSet: AttributeSet? = null) : AndExoPlayerView(context, attributeSet) {

    fun setEndPadding(value: Float) {
        setPadding(0, 0, value.toInt(), 0)
    }
}