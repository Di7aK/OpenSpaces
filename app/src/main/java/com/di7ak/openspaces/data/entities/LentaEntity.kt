package com.di7ak.openspaces.data.entities

class LentaEntity (
    var items: List<LentaItemEntity> = listOf(),
    var nextLinkUrl: String = ""
): BaseEntity()