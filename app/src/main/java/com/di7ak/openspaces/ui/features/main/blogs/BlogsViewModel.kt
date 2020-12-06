package com.di7ak.openspaces.ui.features.main.blogs

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.di7ak.openspaces.data.EVENT_TYPE_DIARY
import com.di7ak.openspaces.data.EVENT_TYPE_DIARY_COMM
import com.di7ak.openspaces.data.EVENT_TYPE_FORUM
import com.di7ak.openspaces.data.EVENT_TYPE_FORUM_COMM
import com.di7ak.openspaces.data.entities.LentaEntity
import com.di7ak.openspaces.data.entities.LentaItemEntity
import com.di7ak.openspaces.data.repository.BlogsRepository
import com.di7ak.openspaces.data.repository.VoteRepository
import com.di7ak.openspaces.utils.Resource
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class BlogsViewModel @ViewModelInject constructor(
    private val blogsRepository: BlogsRepository,
    private val voteRepository: VoteRepository
) : ViewModel() {
    private val _events = MutableLiveData<Resource<LentaEntity>>()
    val events: LiveData<Resource<LentaEntity>> = _events
    private val _updatedEvent = MutableLiveData<LentaItemEntity>()
    val updatedEvent: LiveData<LentaItemEntity> = _updatedEvent
    private var currentPage = 0
    var user: String = ""

    private val filter = arrayOf(
        EVENT_TYPE_DIARY,
        EVENT_TYPE_DIARY_COMM,
        EVENT_TYPE_FORUM,
        EVENT_TYPE_FORUM_COMM
    )

    fun like(lentaModel: LentaItemEntity, up: Boolean) {
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
            _updatedEvent.postValue(lentaModel)
        }
    }

    fun fetch() {
        currentPage = 1
        viewModelScope.launch {
            blogsRepository.fetch(user = user, page = currentPage).collect {

                when (it.status) {
                    Resource.Status.SUCCESS -> {
                        val events = it.data?.items?.filter { event ->
                            event.eventType in filter || event.eventType == 0
                        } ?: listOf()
                        currentPage = 2
                        _events.postValue(Resource.success(LentaEntity(items = events, nextLinkUrl = it.data?.nextLinkUrl ?: "")))
                    }
                    Resource.Status.ERROR -> {
                        _events.postValue(Resource.error(it.message ?: ""))
                    }
                    Resource.Status.LOADING -> {
                        _events.postValue(Resource.loading())
                    }
                    else -> {}
                }
            }
        }
    }

    fun fetchNext() {
        if(events.value?.data?.nextLinkUrl.isNullOrEmpty()) return

        val old = _events.value?.data?.items ?: listOf()
        viewModelScope.launch {
            blogsRepository.fetch(user = user, page = currentPage).collect {
                when (it.status) {
                    Resource.Status.SUCCESS -> {
                        val events = it.data?.items?.filter { event ->
                            event.eventType in filter
                        } ?: listOf()
                        currentPage ++
                        _events.postValue(Resource.success(LentaEntity(items = old + events, nextLinkUrl = it.data?.nextLinkUrl ?: "")))
                    }
                    Resource.Status.ERROR -> {
                        _events.postValue(Resource.error(it.message ?: ""))
                    }
                    Resource.Status.LOADING -> {
                        _events.postValue(Resource.loading())
                    }
                    else -> {}
                }
            }
        }
    }
}