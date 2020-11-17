package com.di7ak.openspaces.utils

import java.util.regex.Pattern

class CommentParser {
    companion object {
        private val pattern = Pattern.compile("itemprop=\"text\">(.+?)</", Pattern.DOTALL)

        fun parse(source: String): String {
            try {
                val matcher = pattern.matcher(source)
                while (matcher.find()) {
                    return matcher.group(1) ?: ""
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return ""
        }
    }
}