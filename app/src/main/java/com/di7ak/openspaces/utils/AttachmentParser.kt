package com.di7ak.openspaces.utils

import com.di7ak.openspaces.data.entities.Attach
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import org.json.JSONObject
import java.util.regex.Pattern

class AttachmentParser(val remoteConfig: FirebaseRemoteConfig) {
    private val pattern = Pattern.compile("<script type=\"spaces/file\">(.+?)</script>", Pattern.DOTALL)

    fun parse(source: String) : Parsed {
        var temp = source
        val attachments = mutableListOf<Attach>()
            try {
                    val matcher = pattern.matcher(source)
                    while (matcher.find()) {
                        val m0 = matcher.group(0)
                        val m1 = matcher.group(1)

                        temp = temp.replace(m0!!, "")

                        val json = JSONObject(m1!!)
                        val mapData = remoteConfig.getString("attach_mapper")
                        val map = JSONObject(mapData)
                        val attach = json.mapJsonTo(Attach::class.java, map)
                        attachments.add(attach)
                    }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        return Parsed(attachments, temp)
    }

    data class Parsed(
        val attachments: List<Attach> = listOf(),
        val clearText: String = ""
    )
}