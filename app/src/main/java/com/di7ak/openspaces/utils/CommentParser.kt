package com.di7ak.openspaces.utils

import android.util.Log
import com.di7ak.openspaces.data.entities.Attach
import java.util.regex.Pattern

class CommentParser {
    companion object {
        private val pattern =
            Pattern.compile("<span itemprop=\"text\">(.+?)</span>", Pattern.DOTALL)

        fun parse(source: String): String {
            try {
                val matcher = pattern.matcher(source)
                while (matcher.find()) {
                    return matcher.group(1) ?: ""
                }
            } catch (e: Exception) {
                Log.d("lol", "", e)
            }
            return ""
        }
    }
}