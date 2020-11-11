package com.di7ak.openspaces.data.entities

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey


@Entity(tableName = "comments")
data class CommentItemEntity(
    @PrimaryKey var id: Int = 0,
    @Embedded
    var author: AuthorEntity? = AuthorEntity(),
    var body: String = "",
    var date: Long = 0L,
    var likes: Int = 0,
    var vote: Int = 0,
    var dislikes: Int = 0,
    var commentsCount: Int = 0,
    var parent: Int = 0,
    var type: Int = 0,
    @Ignore
    var attachments: List<Attach> = listOf(),
    var userId: Int = 0,
    var replyCommentId: Int = 0,
    var replyCommentText: String = "",
    var replyUserName: String = ""
)