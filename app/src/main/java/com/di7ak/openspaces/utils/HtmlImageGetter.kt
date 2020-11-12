package com.di7ak.openspaces.utils

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.Html
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.gif.GifDrawable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.regex.Pattern

class HtmlImageGetter(context: Context): Html.ImageGetter {
    private val pattern = Pattern.compile("(<img\\b|(?!^)\\G)[^>]*?\\b(src|width|height)=([\"']?)([^\"]*)\\3")
    private val glide = Glide.with(context)
    private val density = context.resources.displayMetrics.density
    private val drawables: MutableMap<String, Drawable> = mutableMapOf()

    override fun getDrawable(source: String?) = drawables[source]

    fun preloadFromHtml(scope: CoroutineScope, html: String, callback: () -> Unit, drawableCallback: Drawable.Callback? = null) {
        scope.launch(Dispatchers.IO) {
            val matcher = pattern.matcher(html)
            while (matcher.find()) {
                if (matcher.groupCount() != 0 && matcher.group(1)!!.isNotEmpty()) {
                    if (matcher.group(2) == "src" && matcher.group(4) != null) {
                        val src = matcher.group(4)!!

                        drawables[src] = glide.asDrawable().load(src).submit().get().apply {
                            val width = (intrinsicWidth * density).toInt()
                            val height = (intrinsicHeight * density).toInt()
                            setBounds(0, 0, width, height)
                            if (this is GifDrawable) {
                                start()
                                this.callback = drawableCallback
                            }
                        }
                    }
                }
            }
            callback()
        }
    }
}