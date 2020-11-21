package com.di7ak.openspaces.ui.features.main.profile

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.di7ak.openspaces.data.Session
import com.di7ak.openspaces.data.entities.ProfileEntity
import com.di7ak.openspaces.data.repository.AuthRepository
import com.di7ak.openspaces.data.repository.ProfileRepository
import com.di7ak.openspaces.utils.Resource
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ProfileViewModel @ViewModelInject constructor(
    val session: Session,
    private val authRepository: AuthRepository,
    private val profileRepository: ProfileRepository
) : ViewModel() {
    private val _profile = MutableLiveData<Resource<ProfileEntity?>>()
    val profile: LiveData<Resource<ProfileEntity?>> = _profile

    fun fetchProfile(userId: String) = viewModelScope.launch {
        profileRepository.fetch(userId).collect {
            _profile.value = it
        }
    }

    fun logout() {
        session.current = null
        authRepository.deleteSession(session.current!!)
    }
}