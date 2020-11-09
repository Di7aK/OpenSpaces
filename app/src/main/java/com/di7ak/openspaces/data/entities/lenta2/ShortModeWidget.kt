package com.di7ak.openspaces.data.entities.lenta2

data class ShortModeWidget(
    val dropdown_links: List<DropdownLink>,
    val main_link: MainLink,
    val render_mode: String,
    val render_modes: RenderModesX
)