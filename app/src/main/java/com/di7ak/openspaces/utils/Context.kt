package com.di7ak.openspaces.utils

import android.content.Context
import android.content.res.Configuration
import android.graphics.drawable.Drawable
import android.util.TypedValue
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import com.di7ak.openspaces.R


@ColorInt
fun Context.getColorFromAttr(
    @AttrRes attrColor: Int,
    typedValue: TypedValue = TypedValue(),
    resolveRefs: Boolean = true
): Int {
    theme.resolveAttribute(attrColor, typedValue, resolveRefs)
    return typedValue.data
}

fun Context.getDrawableFromAttr(
    @AttrRes attrDrawable: Int
): Drawable {
    val a = theme.obtainStyledAttributes(R.style.AppTheme, intArrayOf(attrDrawable))
    val attributeResourceId = a.getResourceId(0, 0)
    return ContextCompat.getDrawable(this, attributeResourceId)!!
}

fun Context.isNightMode() : Boolean {
    val config = resources.configuration
    return config.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
}