package com.di7ak.openspaces.ui.features.web

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.di7ak.openspaces.data.entities.AuthAttributes
import com.di7ak.openspaces.data.entities.AuthEntity
import com.di7ak.openspaces.data.repository.AuthRepository
import com.di7ak.openspaces.utils.Resource
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class WebViewModel  @ViewModelInject constructor(
    private val repository: AuthRepository
) : ViewModel() {
    private val _id = MutableLiveData<Int>()
    private val _session = _id.switchMap { id ->
        repository.getSession(id)
    }
    val session: LiveData<AuthAttributes> = _session
    private val _auth = MutableLiveData<Resource<AuthEntity>>()
    val auth: LiveData<Resource<AuthEntity>> = _auth

    fun setUserId(userId: Int) {
        _id.value = userId
    }

    fun check(sid: String = "") = viewModelScope.launch {
        repository.check(
            sid = sid
        ).collect {
            _auth.postValue(it)
        }
    }
}