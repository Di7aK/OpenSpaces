package com.di7ak.openspaces.data.entities.lenta2

data class MetaTags(
    val canonical_URL: String,
    val http_equiv_tags: List<HttpEquivTag>,
    val name_tags: List<Any>,
    val property_tags: List<PropertyTag>
)