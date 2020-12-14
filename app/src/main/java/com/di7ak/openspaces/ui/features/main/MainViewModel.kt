package com.di7ak.openspaces.ui.features.main

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.di7ak.openspaces.data.Session
import com.di7ak.openspaces.data.WebSocket
import com.di7ak.openspaces.data.entities.TopCountEntity
import com.di7ak.openspaces.data.repository.TopCountRepository
import com.di7ak.openspaces.data.repository.WebSocketRepository
import com.di7ak.openspaces.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MainViewModel @ViewModelInject constructor(
    private val topCountRepository: TopCountRepository,
    private val session: Session,
    private val webSocketRepository: WebSocketRepository
) : ViewModel() {
    private val _updatedTopCount = MutableLiveData<TopCountEntity>()
    val updatedTopCount: LiveData<TopCountEntity> = _updatedTopCount

    private val _topCount = MutableLiveData<Resource<TopCountEntity?>>()
    val topCount: LiveData<Resource<TopCountEntity?>> = _topCount
    private var topCountEntity: TopCountEntity = TopCountEntity()

    fun fetchTopCount() = viewModelScope.launch {
        topCountRepository.fetch().collect {
            _topCount.value = it
            it.data?.let { topCount -> topCountEntity = topCount }
            _topCount.postValue(Resource.ready())
        }
    }

    fun startWebSocket() {
        viewModelScope.launch(Dispatchers.IO) {
            webSocketRepository.create(session.current?.channelId ?: "")
            webSocketRepository.topCount().collect { topCount ->
                _updatedTopCount.postValue(
                    topCountEntity.apply {
                        when (topCount.type) {
                            WebSocket.TopCounterTypes.TYPE_JOURNAL -> journalCnt = topCount.cnt
                            WebSocket.TopCounterTypes.TYPE_LENTA -> lentaCnt = topCount.cnt
                            WebSocket.TopCounterTypes.TYPE_MAIL -> mailCnt = topCount.cnt
                        }
                    }
                )
            }
        }
    }
}
