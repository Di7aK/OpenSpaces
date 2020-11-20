package com.di7ak.openspaces.ui.features.auth.login

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.di7ak.openspaces.data.Session
import com.di7ak.openspaces.data.entities.AuthAttributes
import com.di7ak.openspaces.data.entities.AuthEntity
import com.di7ak.openspaces.data.repository.AuthRepository
import com.di7ak.openspaces.utils.Resource
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class LoginViewModel @ViewModelInject constructor(
    private val repository: AuthRepository,
    private val session: Session
) : ViewModel() {
    private val _auth = MutableLiveData<Resource<AuthEntity>>()
    val auth: LiveData<Resource<AuthEntity>> = _auth
    var currentSession: AuthAttributes?
        set(value) { session.current = value }
        get() = session.current

    fun login(login: String, password: String, code: String = "") = viewModelScope.launch {
        repository.login(
            login = login,
            password = password,
            code = code
        ).collect {
            _auth.postValue(it)
        }
    }

}
