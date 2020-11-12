package com.di7ak.openspaces.utils

import android.graphics.drawable.Drawable
import android.os.Build
import android.text.Html
import android.text.Spanned
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun String?.fromHtml(
    scope: CoroutineScope,
    imageGetter: HtmlImageGetter,
    callback: (Spanned) -> Unit,
    drawableCallback: Drawable.Callback? = null
) {
    fun setHtml() {
        val html = this ?: ""
        val result = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY, imageGetter, null)
        } else {
            @Suppress("deprecation")
            Html.fromHtml(html, imageGetter, null)
        }
        callback(result)
    }
    setHtml()
    if (this == null) return

    imageGetter.preloadFromHtml(scope, this, {
        scope.launch(Dispatchers.Main) { setHtml() }
    }, drawableCallback)
}