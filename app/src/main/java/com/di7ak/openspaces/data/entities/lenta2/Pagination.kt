package com.di7ak.openspaces.data.entities.lenta2

data class Pagination(
    val count: String,
    val current_page: String,
    val flags: String,
    val items_on_page: String,
    val last_page: String,
    val nears: String,
    val next_link_url: String,
    val numbered: String,
    val numbered_links: List<NumberedLink>,
    val page_param: String,
    val url: String
)