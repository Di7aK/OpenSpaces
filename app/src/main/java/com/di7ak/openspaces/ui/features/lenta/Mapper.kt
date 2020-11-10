package com.di7ak.openspaces.ui.features.lenta

import android.net.Uri
import com.di7ak.openspaces.data.EVENT_TYPE_DIARY
import com.di7ak.openspaces.data.EVENT_TYPE_FORUM
import com.di7ak.openspaces.data.entities.lenta2.Events
import com.di7ak.openspaces.utils.fromHtml

fun Events.toLentaModel() : LentaModel {
    val profileImage = author_avatar.previewURL
    val detail = items.first()

    val id = detail.id ?: detail.nid ?: 0

    var type = detail.type
    if(type == 0) {
        val link = detail.links.bookmark_link
        val uri = Uri.parse(link.fromHtml().toString())
        type = uri.getQueryParameter("object_type")?.toIntOrNull() ?: 0
    }

    val idSource1 = detail.userWidget?.siteLink?.id
    val idSource2 = author_widget?.siteLink?.id
    val idSource3 = author_widget?.id
    val profileId = idSource1 ?: idSource2 ?: idSource3 ?: 0

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
        id = id,
        author = Author(profileId, userName, profileImage),
        title = title,
        body = body,
        date = date,
        likes = likes,
        liked = liked,
        dislikes = dislikes,
        disliked = disliked,
        commentsCount = commentsCount,
        eventType = event_type,
        type = type
    )
}