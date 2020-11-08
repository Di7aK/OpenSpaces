package com.di7ak.openspaces.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

data class AuthEntity (
    @SerializedName("attributes") val attributes: AuthAttributes?
) : BaseEntity()

@Entity(tableName = "sessions")
data class AuthAttributes(
    @SerializedName("name") val name: String = "",
    @SerializedName("confirm_phone") val confirmPhone: Int = 0,
    @SerializedName("mysite_link") val mysiteLink: String = "",
    @SerializedName("avatar") val avatar: String = "",
    @SerializedName("sid") val sid: String = "",
    @SerializedName("CK") val ck: String = "",
    @PrimaryKey @SerializedName("nid") val userId: Int = 0,
    @SerializedName("channel_id") val channelId: String = ""
)