package com.di7ak.openspaces.data.entities

data class JournalEntity(
    var items: List<JournalItemEntity> = listOf(),
    var countNew: Int? = 0,
    var countMain: Int? = 0
) : BaseEntity()