package com.di7ak.openspaces.ui.features.main.comments

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.di7ak.openspaces.data.ObjectConst
import com.di7ak.openspaces.data.entities.CommentItemEntity
import com.di7ak.openspaces.data.entities.LentaItemEntity
import com.di7ak.openspaces.data.local.CommentsDao
import com.di7ak.openspaces.data.repository.CommentsRepository
import com.di7ak.openspaces.data.repository.VoteRepository
import com.di7ak.openspaces.utils.Resource
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class CommentViewModel @ViewModelInject constructor(
    private val commentsRepository: CommentsRepository,
    private val voteRepository: VoteRepository,
    private val commentsDao: CommentsDao,
    private val remoteConfig: FirebaseRemoteConfig
) : ViewModel() {
    private val _comments = MutableLiveData<Resource<List<CommentItemEntity>>>()
    val comments: LiveData<Resource<List<CommentItemEntity>>> = _comments
    private val _comment = MutableLiveData<Resource<CommentItemEntity>>()
    val comment: LiveData<Resource<CommentItemEntity>> = _comment
    private val _updatedComment = MutableLiveData<CommentItemEntity>()
    val updatedComment: LiveData<CommentItemEntity> = _updatedComment
    private val _deletedComment = MutableLiveData<CommentItemEntity>()
    val deletedComment: LiveData<CommentItemEntity> = _deletedComment
    private val _editComment = MutableLiveData<CommentItemEntity>()
    val editComment: LiveData<CommentItemEntity> = _editComment
    var post: LentaItemEntity? = null
    var url: String? = null
    var guestBookUser: Int = 0
    set(value) {
        field = value
        val base = remoteConfig.getString("base_url")
        url = "$base/guestbook/index/$value"
    }
    var replyTo: Int? = null
    var edit: CommentItemEntity? = null

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

    fun fetch() {
        val id = post?.id ?: guestBookUser
        val url = post?.commentUrl ?: url ?: ""
        viewModelScope.launch {
            commentsRepository.fetch(id, url).collect {
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
                    else -> {}
                }
            }
        }
    }

    fun add(comment: String) = viewModelScope.launch {
        if(edit != null) {
            edit(comment)
            return@launch
        }
        val id = post?.id ?: if(guestBookUser != 0) guestBookUser else 0
        val type = ObjectConst.OBJECT_TYPE_TO_COMMENT_TYPE[post?.type ?: 0] ?: if(guestBookUser != 0) 14 else 0

        commentsRepository.add(
            postId = id, type=  type, comment = comment, cr = replyTo ?: 0).collect { comment ->
            replyTo?.let { replyId ->
                comment.data?.replyCommentId = replyId
                _comments.value?.data?.find { it.id != 0 && it.id == replyId }?.let { replyComment ->
                    comment.data?.replyCommentText = replyComment.body
                    comment.data?.replyUserName = replyComment.author?.name ?: ""
                }
            }
            _comment.postValue(comment)
        }
    }

    fun delete(comment: CommentItemEntity) = viewModelScope.launch {
        commentsRepository.delete(comment.type, comment.id).collect {
            if (it.status == Resource.Status.SUCCESS) {
                _comments.value?.data?.toMutableList()?.apply {
                    removeAll { item -> item.id == comment.id }
                }?.toList() ?: listOf()
                _deletedComment.postValue(comment)
            }
        }
    }

    private fun edit(comment: String) = viewModelScope.launch {
        commentsRepository.edit(edit!!.type, edit!!.id, comment).collect {
            if(it.status == Resource.Status.SUCCESS) {
                edit?.apply {
                    body = comment
                    _editComment.postValue(this)
                    edit = null
                }
            }
        }
    }
}