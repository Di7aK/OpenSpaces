package com.di7ak.openspaces.data.entities.lenta2

data class DownloadBox(
    val downloadByBitrate: List<DownloadByBitrate>,
    val downloadCaption: String,
    val downloadLink: DownloadLink,
    val downloadURL: String,
    val duration: String,
    val nid: String,
    val playURL: String,
    val renderType: String,
    val showLinks: String,
    val type: String,
    val weight: String
)