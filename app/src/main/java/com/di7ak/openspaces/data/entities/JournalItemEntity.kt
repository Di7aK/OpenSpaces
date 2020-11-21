package com.di7ak.openspaces.data.entities

data class JournalItemEntity(
    var id: Int? = 0,
    var text: String? = "",
    var answer: Int? = 0,
    var colorType: Int? = 0,
    var commentsCnt: Int? = 0,
    var commentUserName: String? = "",
    var message: String? = "",
    var link: String? = "",
    var header: String? = "",
    var date: String? = ""
)