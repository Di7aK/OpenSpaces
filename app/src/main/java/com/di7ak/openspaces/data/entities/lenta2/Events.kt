package com.di7ak.openspaces.data.entities.lenta2

data class Events(
    val Rli: String,
    val Sort: String,
    val author_avatar: AuthorAvatar,
    val author_filter_link: AuthorFilterLink,
    val author_gender: String,
    val author_genders: AuthorGenders,
    val author_id: String,
    val author_type: String,
    val author_widget: AuthorWidget?,
    val date: String,
    val delete_URL: String,
    val event_type: Int,
    val event_types: EventTypes,
    val id: Int,
    val items: List<ItemX>,
    val items_cnt: String,
    val more_items_link: MoreItemsLink,
    val `object`: String,
    val preview_items: List<PreviewItem>,
    val reader_id: String,
    val share: String,
    val sort_value: String,
    val tile_items: List<TileItem>
)