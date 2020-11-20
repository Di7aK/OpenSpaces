package com.di7ak.openspaces.data.entities

data class ProfileEntity(
    var avatarPreview: String? = null,
    var avatarUrl: String? = null,
    var fullName: String? = null,
    var status: String? = null,
    var name: String? = null
) : BaseEntity()