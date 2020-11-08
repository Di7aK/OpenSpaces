package com.di7ak.openspaces.data.entities.lenta

data class Events(
    val Rli: String,
    val Sort: String,
    val author_avatar: AuthorAvatar,
    val author_filter_link: AuthorFilterLink,
    val author_id: String,
    val author_type: String,
    val author_widget: AuthorWidget,
    val date: String,
    val delete_URL: String,
    val event_type: Int,
    val event_types: EventTypes,
    val id: String,
    val items: List<ItemX>,
    val reader_id: String,
    val sort_value: String
)