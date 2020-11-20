package com.di7ak.openspaces.ui.features.main

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.di7ak.openspaces.data.Session
import com.di7ak.openspaces.data.entities.TopCountEntity
import com.di7ak.openspaces.data.repository.TopCountRepository
import com.di7ak.openspaces.utils.Resource
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


class MainViewModel @ViewModelInject constructor(
    private val topCountRepository: TopCountRepository,
    private val session: Session
) : ViewModel() {
    private val _topCount = MutableLiveData<Resource<TopCountEntity?>>()
    val topCount: LiveData<Resource<TopCountEntity?>> = _topCount

    fun fetchTopCount() = viewModelScope.launch {
        topCountRepository.fetch().collect {
            _topCount.value = it
            _topCount.postValue(Resource.ready())
        }
    }
}
