package com.di7ak.openspaces.data.entities

class CommentsEntity (
    var items: List<CommentItemEntity> = listOf(),
    var commentsCount: Int = 0
): BaseEntity()