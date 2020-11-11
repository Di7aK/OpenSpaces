package com.di7ak.openspaces.ui.features.comments

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.di7ak.openspaces.data.entities.CommentItemEntity
import com.di7ak.openspaces.data.local.CommentsDao
import com.di7ak.openspaces.data.repository.CommentsRepository
import com.di7ak.openspaces.data.repository.VoteRepository
import com.di7ak.openspaces.utils.Resource
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class CommentViewModel @ViewModelInject constructor(
    private val commentsRepository: CommentsRepository,
    private val voteRepository: VoteRepository,
    private val commentsDao: CommentsDao
) : ViewModel() {
    private val _comments = MutableLiveData<Resource<List<CommentItemEntity>>>()
    val comments: LiveData<Resource<List<CommentItemEntity>>> = _comments
    private val _updatedComment = MutableLiveData<CommentItemEntity>()
    val updatedComment: LiveData<CommentItemEntity> = _updatedComment

    fun like(commentModel: CommentItemEntity, up: Boolean) {
        viewModelScope.launch {
            val start = System.currentTimeMillis()
            val minDelay = 500L
            val isChange = (commentModel.vote == 1 && !up) || (commentModel.vote == -1 && up)
            val isNewVote = commentModel.vote != 1 && commentModel.vote != -1
            if(!isChange && !isNewVote) {
                voteRepository.unlike(commentModel.id, commentModel.type).collect {
                    if(it.status == Resource.Status.SUCCESS) {
                        commentModel.vote = 0
                        if (up) {
                            commentModel.likes --
                        } else {
                            commentModel.dislikes --
                        }
                    }
                }
            } else {
                voteRepository.like(commentModel.id, commentModel.type, !up).collect {
                    if(it.status == Resource.Status.SUCCESS) {
                        if (isChange) {
                            if (up) {
                                commentModel.likes ++
                                commentModel.vote = 1
                                commentModel.dislikes --
                            } else {
                                commentModel.likes --
                                commentModel.vote = -1
                                commentModel.dislikes ++
                            }
                        } else {
                            if (up) {
                                commentModel.vote = 1
                                commentModel.likes ++
                            } else {
                                commentModel.vote = -1
                                commentModel.dislikes ++
                            }
                        }
                    }
                }
            }
            delay(minDelay - (System.currentTimeMillis() - start))
            _updatedComment.postValue(commentModel)
            commentsDao.insert(commentModel)
        }
    }

    fun fetch(postId: Int, url: String) {
        viewModelScope.launch {
            commentsRepository.fetch(postId, url).collect {
                when (it.status) {
                    Resource.Status.SUCCESS -> {
                        _comments.postValue(Resource.success(it.data!!))
                    }
                    Resource.Status.ERROR -> {
                        _comments.postValue(Resource.error(it.message ?: ""))
                    }
                    Resource.Status.LOADING -> {
                        _comments.postValue(Resource.loading())
                    }
                }
            }
        }
    }
}