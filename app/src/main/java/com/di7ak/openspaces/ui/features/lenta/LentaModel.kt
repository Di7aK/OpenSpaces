package com.di7ak.openspaces.ui.features.lenta

import androidx.room.*

@Entity(tableName = "lenta")
data class LentaModel(
    @PrimaryKey var id: Int? = 0,
    @Embedded
    var author: Author? = Author(),
    var title: String? = "",
    var body: String? = "",
    var date: Long? = 0L,
    var likes: Int? = 0,
    var liked: Boolean? = false,
    var dislikes: Int? = 0,
    var disliked: Boolean? = false,
    var commentsCount: Int? = 0,
    var type: Int? = 0,

    var userId: Int = 0
)

@Entity(tableName = "authors")
data class Author(
    @ColumnInfo(name = "author_id")
    @PrimaryKey var id: Long? = 0,
    var name: String? = "",
    var profileImage: String? = ""
)