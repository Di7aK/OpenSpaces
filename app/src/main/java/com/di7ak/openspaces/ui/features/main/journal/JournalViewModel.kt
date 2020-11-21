package com.di7ak.openspaces.ui.features.main.journal

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.di7ak.openspaces.data.JOURNAL_FILTER_NEW
import com.di7ak.openspaces.data.entities.JournalItemEntity
import com.di7ak.openspaces.data.repository.JournalRepository
import com.di7ak.openspaces.utils.Resource
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class JournalViewModel @ViewModelInject constructor(
    private val journalRepository: JournalRepository
) : ViewModel() {
    private val _records = MutableLiveData<Resource<List<JournalItemEntity>>>()
    val records: LiveData<Resource<List<JournalItemEntity>>> = _records
    var filter: Int = JOURNAL_FILTER_NEW
    set(value) {
        field = value
        fetch()
    }
    private val limit = 30
    private val offset = 0

    fun fetch() {
        viewModelScope.launch {
            journalRepository.fetch(limit, offset, filter).collect {
                when (it.status) {
                    Resource.Status.SUCCESS -> {
                        _records.postValue(Resource.success(it.data!!.items))
                    }
                    Resource.Status.ERROR -> {
                        _records.postValue(Resource.error(it.message ?: ""))
                    }
                    Resource.Status.LOADING -> {
                        _records.postValue(Resource.loading())
                    }
                    else -> {}
                }
            }
        }
    }
}