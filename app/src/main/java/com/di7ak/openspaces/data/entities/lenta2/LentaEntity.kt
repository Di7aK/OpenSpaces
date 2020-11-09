package com.di7ak.openspaces.data.entities.lenta2

data class LentaEntity(
    val author_id: Any,
    val author_type: Any,
    val back_line: BackLine,
    val bottom_links: BottomLinks,
    val code: String,
    val current_link_id: String,
    val device_type: Int,
    val events_cnt: String,
    val events_list: List<Events>,
    val hits: Int,
    val items_on_page: String,
    val lenta_loading: Any,
    val list: String,
    val meta_tags: MetaTags,
    val pagination: Pagination,
    val reklama_block1: ReklamaBlock1,
    val revision: List<String>,
    val short_mode_widget: ShortModeWidget,
    val top_widget: TopWidget
)