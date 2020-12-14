package com.di7ak.openspaces.data.remote

import android.util.Log
import com.di7ak.openspaces.data.entities.UpdatedTopCount
import kotlinx.coroutines.flow.*
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import org.json.JSONObject

class WebSocketDataSource : WebSocketListener() {
    private var _flow: MutableStateFlow<JSONObject> = MutableStateFlow(JSONObject())

    override fun onMessage(webSocket: WebSocket, text: String) {
        super.onMessage(webSocket, text)
        _flow.value = JSONObject(text)
        Log.d("lol", text)
    }

    private var flow: Flow<JSONObject>? = null

    fun listen(): StateFlow<JSONObject> = _flow

    fun topCount(): Flow<UpdatedTopCount> = flow {
        _flow.filter { it.has("text") }
        .map { it.getJSONObject("text") }
        .filter {
            it.has("act")
                    && it.getInt("act") == com.di7ak.openspaces.data.WebSocket.Actions.WEB_SOCKET_ACTION_TOP_COUNTER_UPDATE
        }
        .collect {
            emit(UpdatedTopCount().apply {
                cnt = it.getInt("cnt")
                type = it.getInt("type")
            })
        }
    }
}