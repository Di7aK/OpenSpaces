package com.di7ak.openspaces.data.entities

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "authors")
data class AuthorEntity(
    @ColumnInfo(name = "author_id")
    @PrimaryKey var id: Long? = 0,
    var name: String = "",
    var profileImage: String = ""
): Parcelable