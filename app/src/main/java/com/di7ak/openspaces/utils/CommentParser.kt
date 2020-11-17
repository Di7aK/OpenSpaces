package com.di7ak.openspaces.utils

import android.util.Log
import java.util.regex.Pattern

class CommentParser {
    companion object {
        private val pattern = Pattern.compile("<span itemprop=\"text\">(.+?)</span>", Pattern.DOTALL)
        private val pattern2 = Pattern.compile("<div itemprop=\"text\">(.+?)</div>", Pattern.DOTALL)
        private val pattern3 = Pattern.compile("<div class=\"text-block11 nl\" itemprop=\"text\">(.+?)</div>", Pattern.DOTALL)

        fun parse(source: String): String {
            try {
                var matcher = pattern.matcher(source)
                while (matcher.find()) {
                    return matcher.group(1) ?: ""
                }
                matcher = pattern2.matcher(source)
                while (matcher.find()) {
                    return matcher.group(1) ?: ""
                }
                matcher = pattern3.matcher(source)
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