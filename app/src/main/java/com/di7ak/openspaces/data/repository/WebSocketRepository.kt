package com.di7ak.openspaces.data.repository

import com.di7ak.openspaces.data.remote.WebSocketDataSource
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket

class WebSocketRepository(private val client: OkHttpClient, private val webSocketDataSource: WebSocketDataSource) {
    lateinit var ws: WebSocket
    fun create(channelId: String) {
        if(::ws.isInitialized) ws.close(1001, "")

        val url = "wss://lp03.spac.me/ws/$channelId"
        val request = Request.Builder().url(url).build()
        ws = client.newWebSocket(request, webSocketDataSource)
    }

    fun listen() = webSocketDataSource.listen()

    fun topCount() = webSocketDataSource.topCount()
}