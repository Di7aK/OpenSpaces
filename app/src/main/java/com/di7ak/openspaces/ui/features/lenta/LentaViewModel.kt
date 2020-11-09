package com.di7ak.openspaces.ui.features.lenta

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.di7ak.openspaces.data.*
import com.di7ak.openspaces.data.local.LentaDao
import com.di7ak.openspaces.data.repository.LentaRepository
import com.di7ak.openspaces.data.repository.VoteRepository
import com.di7ak.openspaces.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

class LentaViewModel @ViewModelInject constructor(
    private val lentaRepository: LentaRepository,
    private val lentaDao: LentaDao,
    private val voteRepository: VoteRepository,
    private val session: Session
) : ViewModel() {
    private val _events = MutableLiveData<Resource<List<LentaModel>>>()
    val events: LiveData<Resource<List<LentaModel>>> = _events

    private val filter = arrayOf(
        EVENT_TYPE_DIARY,
        EVENT_TYPE_DIARY_COMM,
        EVENT_TYPE_FORUM,
        EVENT_TYPE_FORUM_COMM
    )

    fun like(lentaModel: LentaModel, up: Boolean) {
        viewModelScope.launch {
            val down = if(up) 0 else 1
            voteRepository.like(lentaModel.id ?: 0, lentaModel.type ?: 0, down).collect {
                if(lentaModel.liked && down == 0) {
                    lentaModel.liked = false
                    lentaModel.likes --
                } else if(lentaModel.disliked && down == 1) {
                    lentaModel.disliked = false
                    lentaModel.dislikes --
                } else if(!lentaModel.liked && down == 0) {
                    lentaModel.liked = true
                    lentaModel.likes ++
                } else if(!lentaModel.disliked && down == 1) {
                    lentaModel.disliked = false
                    lentaModel.dislikes ++
                }
                lentaDao.insert(lentaModel)
            }
        }
    }

    fun fetch() {
        viewModelScope.launch {
            lentaDao.getEvents(session.current?.userId ?: 0).flowOn(Dispatchers.IO).collect {
                if (it.isNotEmpty()) _events.postValue(Resource.success(it))
            }
        }
        viewModelScope.launch {
            lentaRepository.fetch().collect {
                when (it.status) {
                    Resource.Status.SUCCESS -> {
                        val events = it.data?.events_list?.filter { event ->
                            event.event_type in filter
                        }?.map { event ->
                            event.toLentaModel().apply {
                                userId = session.current?.userId ?: 0
                            }
                        } ?: listOf()
                        lentaDao.insertAll(events)
                        _events.postValue(Resource.success(events))
                    }
                    Resource.Status.ERROR -> {
                        _events.postValue(Resource.error(it.message ?: ""))
                    }
                    Resource.Status.LOADING -> {
                        _events.postValue(Resource.loading())
                    }
                }
            }
        }
    }
}