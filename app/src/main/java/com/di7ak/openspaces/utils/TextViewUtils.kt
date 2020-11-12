package com.di7ak.openspaces.utils

import android.graphics.drawable.Drawable
import android.widget.TextView

fun TextView.createDrawableCallback() = object : Drawable.Callback {
        override fun invalidateDrawable(who: Drawable) {
            invalidate()
        }

        override fun scheduleDrawable(who: Drawable, what: Runnable, `when`: Long) {
            postDelayed(what, `when`)
        }

        override fun unscheduleDrawable(who: Drawable, what: Runnable) {
            removeCallbacks(what)
        }

    }