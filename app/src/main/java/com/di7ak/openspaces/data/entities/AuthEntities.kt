package com.di7ak.openspaces.data.entities

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

data class AuthEntity (
    var attributes: AuthAttributes? = AuthAttributes()
) : BaseEntity()

@Entity(tableName = "sessions")
data class AuthAttributes(
    var name: String = "",
    var confirmPhone: Int = 0,
    var mysiteLink: String = "",
    var avatar: String = "",
    var sid: String = "",
    var ck: String = "",
    @PrimaryKey var userId: Int = 0,
    var channelId: String = "",
    @Ignore
    var progress: Boolean = false
)