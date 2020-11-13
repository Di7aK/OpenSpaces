package com.di7ak.openspaces.data.entities

import androidx.room.*

@Entity(tableName = "lenta")
data class LentaItemEntity(
    @PrimaryKey var id: Int = 0,
    @Embedded
    var author: AuthorEntity? = AuthorEntity(),
    var title: String = "",
    var body: String = "",
    var date: Long = 0L,
    var likes: Int = 0,
    var liked: Boolean = false,
    var dislikes: Int = 0,
    var disliked: Boolean = false,
    var commentsCount: Int = 0,
    var eventType: Int = 0,
    var type: Int = 0,
    var commentUrl: String = "",
    @Ignore
    var attachments: List<Attach> = listOf(),
    var userId: Int = 0
)

@Entity(tableName = "attachments")
data class Attach(
    @ColumnInfo(name = "attach_id")
    @PrimaryKey var id: Long? = 0,
    var url: String = "",
    var previewUrl: String = "",
    var height: Int = 0,
    var external: Int = 0,
    var sourceType: Int = 0,
    var videoId: String = "",
    var type: Int = 0,
    var parent: Int = 0
)