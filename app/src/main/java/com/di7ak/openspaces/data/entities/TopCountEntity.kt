package com.di7ak.openspaces.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "top_count")
data class TopCountEntity(
    @PrimaryKey var userId: Int = 0,
    var lentaCnt: Int = 0,
    var journalCnt: Int = 0,
    var mailCnt: Int = 0,
    var avatarUrl: String = "",
    var color: String = "",
    var focusColor: String = ""
): BaseEntity()