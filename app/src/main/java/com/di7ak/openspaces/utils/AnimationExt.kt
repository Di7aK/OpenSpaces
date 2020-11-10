package com.di7ak.openspaces.utils

import android.view.animation.Animation

fun Animation.onEnd(callback: () -> Unit) {
    setAnimationListener(object : Animation.AnimationListener {
        override fun onAnimationRepeat(animation: Animation?) {

        }

        override fun onAnimationEnd(animation: Animation?) {
            callback()
        }

        override fun onAnimationStart(animation: Animation?) {

        }
    })
}