package com.di7ak.openspaces.ui.features.auth

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.di7ak.openspaces.data.entities.AuthAttributes
import com.di7ak.openspaces.data.repository.AuthRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class AuthViewModel @ViewModelInject constructor(
    private val repository: AuthRepository
) : ViewModel() {
    private val _sessions = MutableLiveData<List<AuthAttributes>>()
    val sessions: LiveData<List<AuthAttributes>> = _sessions

    fun fetchSessions() = viewModelScope.launch {
        repository.getSessions().collect{
            _sessions.postValue(it)
        }
    }
}