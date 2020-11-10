package com.di7ak.openspaces.ui.features.lenta

import android.util.Log
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
import kotlinx.coroutines.delay
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
            val start = System.currentTimeMillis()
            val minDelay = 500L
            val isChange = (lentaModel.liked && !up) || (lentaModel.disliked && up)
            val isNewVote = !lentaModel.liked && !lentaModel.disliked
            if(!isChange && !isNewVote) {
                voteRepository.unlike(lentaModel.id, lentaModel.type).collect {
                    if(it.status == Resource.Status.SUCCESS) {
                            if (up) {
                                lentaModel.liked = false
                                lentaModel.likes --
                            } else {
                                lentaModel.disliked = false
                                lentaModel.dislikes --
                            }
                        }
                }
            } else {
                voteRepository.like(lentaModel.id, lentaModel.type, !up).collect {
                    if(it.status == Resource.Status.SUCCESS) {
                        if (isChange) {
                            if (up) {
                                lentaModel.liked = true
                                lentaModel.likes ++
                                lentaModel.disliked = false
                                lentaModel.dislikes --
                            } else {
                                lentaModel.liked = false
                                lentaModel.likes --
                                lentaModel.disliked = true
                                lentaModel.dislikes ++
                            }
                        } else {
                            if (up) {
                                Log.d("okhttp", "like")
                                lentaModel.liked = true
                                lentaModel.likes ++
                            } else {
                                lentaModel.disliked = true
                                lentaModel.dislikes ++
                            }
                        }
                    }
                }
            }
            delay(minDelay - (System.currentTimeMillis() - start))
            lentaDao.insert(lentaModel)
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
                                //Log.d("okhttp", "type ${type}, $this")
                            }
                        } ?: listOf()

                        lentaDao.insertAll(events)
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