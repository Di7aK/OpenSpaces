package com.di7ak.openspaces.ui.features.accounts

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.di7ak.openspaces.data.Session
import com.di7ak.openspaces.data.entities.AuthAttributes
import com.di7ak.openspaces.data.entities.TopCountEntity
import com.di7ak.openspaces.data.repository.AuthRepository
import com.di7ak.openspaces.data.repository.TopCountRepository
import com.di7ak.openspaces.utils.Resource
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class AccountsViewModel @ViewModelInject constructor(
    private val repository: AuthRepository,
    private val topCountRepository: TopCountRepository,
    private val session: Session
) : ViewModel() {
    private val _sessions = MutableLiveData<List<AuthAttributes>>()
    val sessions: LiveData<List<AuthAttributes>> = _sessions
    private val _topCount = MutableLiveData<Resource<TopCountEntity?>>()
    val topCount: LiveData<Resource<TopCountEntity?>> = _topCount
    var currentSession: AuthAttributes?
        set(value) { session.current = value }
        get() = session.current

    fun fetchSessions() = viewModelScope.launch {
        repository.getSessions().collect{
            _sessions.postValue(it)
        }
    }

    fun deleteSession(session: AuthAttributes) {
        repository.deleteSession(session)
    }
    
    fun fetchTopCount() = viewModelScope.launch {
        topCountRepository.fetch().collect {
            _topCount.postValue(it)
        }
    }
}