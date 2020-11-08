package com.di7ak.openspaces.ui.features.lenta

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.di7ak.openspaces.data.EVENT_TYPE_DIARY
import com.di7ak.openspaces.data.EVENT_TYPE_DIARY_COMM
import com.di7ak.openspaces.data.EVENT_TYPE_FORUM
import com.di7ak.openspaces.data.EVENT_TYPE_FORUM_COMM
import com.di7ak.openspaces.data.entities.lenta.Events
import com.di7ak.openspaces.data.repository.AuthRepository
import com.di7ak.openspaces.data.repository.LentaRepository
import com.di7ak.openspaces.utils.Resource
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class LentaViewModel @ViewModelInject constructor(
    private val lentaRepository: LentaRepository,
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _events = MutableLiveData<List<Events>>()
    val events: LiveData<List<Events>> = _events

    fun setUser(userId: Int) {
        viewModelScope.launch {
            val session = authRepository.getSession(userId)
            lentaRepository.fetch(session.sid).collect {
                if(it.status == Resource.Status.SUCCESS) {
                    _events.postValue(
                        it.data!!.events_list.filter { it.event_type == EVENT_TYPE_DIARY || it.event_type == EVENT_TYPE_DIARY_COMM
                                || it.event_type == EVENT_TYPE_FORUM || it.event_type == EVENT_TYPE_FORUM_COMM}
                    )
                }
            }
        }
    }
}