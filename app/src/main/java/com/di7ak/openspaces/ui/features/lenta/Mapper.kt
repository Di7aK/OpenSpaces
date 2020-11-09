package com.di7ak.openspaces.ui.features.lenta

import com.di7ak.openspaces.data.EVENT_TYPE_DIARY
import com.di7ak.openspaces.data.EVENT_TYPE_FORUM
import com.di7ak.openspaces.data.entities.lenta2.Events

fun Events.toLentaModel() : LentaModel {
    val profileImage = author_avatar.previewURL
    val detail = items.first()

    val id = detail.id
    val type = detail.type
    val idSource1 = detail.userWidget?.siteLink?.id
    val idSource2 = author_widget?.siteLink?.id
    val profileId = idSource1 ?: idSource2 ?: 0

    val nameSource1 = detail.userWidget?.siteLink?.user_name
    val nameSource2 = detail.userWidget?.name
    val nameSource3 = author_widget?.siteLink?.user_name
    val nameSource4 = detail.commWidget?.name
    val userName = nameSource1 ?: nameSource2 ?: nameSource3 ?: nameSource4 ?: ""

    var title = ""
    var body = ""
    if (event_type == EVENT_TYPE_FORUM) {
        title = detail.subject?.toString() ?: ""
        body = detail.description
    } else if(event_type == EVENT_TYPE_DIARY) {
        title = detail.header
        body = detail.subject?.toString() ?: ""
    }

    val likes = detail.likes
    val liked = detail.liked == 1

    val dislikes = detail.dislikes
    val disliked = detail.disliked == 1

    val date = sort_value.toLong() * 1000

    val commentsCount = detail.comments

    return LentaModel(
        id,
        Author(profileId, userName, profileImage),
        title,
        body,
        date,
        likes,
        liked,
        dislikes,
        disliked,
        commentsCount,
        event_type,
        type
    )
}