package com.di7ak.openspaces.ui.features.auth

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.di7ak.openspaces.data.entities.AuthEntity
import com.di7ak.openspaces.data.repository.AuthRepository
import com.di7ak.openspaces.utils.Resource
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class AuthViewModel @ViewModelInject constructor(
    private val repository: AuthRepository
) : ViewModel() {
    private val _auth = MutableLiveData<Resource<AuthEntity>>()
    val auth: LiveData<Resource<AuthEntity>> = _auth

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
