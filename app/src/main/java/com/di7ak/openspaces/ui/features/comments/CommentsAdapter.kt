package com.di7ak.openspaces.ui.features.comments

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.isGone
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.di7ak.openspaces.R
import com.di7ak.openspaces.data.ATTACH_TYPE_INTERNAL_IMAGE
import com.di7ak.openspaces.data.entities.Attach
import com.di7ak.openspaces.data.entities.CommentItemEntity
import com.di7ak.openspaces.databinding.ItemCommentBinding
import com.di7ak.openspaces.utils.DateUtils
import com.di7ak.openspaces.utils.fromHtml
import java.util.concurrent.TimeUnit

class CommentsAdapter(private val listener: CommentsItemListener) :
    RecyclerView.Adapter<CommentViewHolder>() {

    interface CommentsItemListener {
        fun onClickedItem(view: View, item: CommentItemEntity)
        fun onClickedLike(view: View, item: CommentItemEntity)
        fun onClickedDislike(view: View, item: CommentItemEntity)
        fun onClickedAttach(view: View, item: Attach)
    }

    private val items = mutableListOf<CommentItemEntity>()

    fun setItems(items: ArrayList<CommentItemEntity>) {
        this.items.clear()
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    fun updateItem(item: CommentItemEntity) = items.indexOf(item).apply {
        if(this != -1) notifyItemChanged(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val binding: ItemCommentBinding =
            ItemCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CommentViewHolder(binding, listener)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) =
        holder.bind(items[position])
}

class CommentViewHolder(
    private val itemBinding: ItemCommentBinding,
    private val listener: CommentsAdapter.CommentsItemListener
) : RecyclerView.ViewHolder(itemBinding.root),
    View.OnClickListener {
    private lateinit var comment: CommentItemEntity
    private var animationVoteUp: Animation
    private var animationVoteDown: Animation
    private var drawableLikeColored: Drawable
    private var drawableDislikeColored: Drawable
    private var drawableLike: Drawable
    private var drawableDislike: Drawable
    private var colorDislike: Int
    private var colorLike: Int
    private var colorNormal: Int

    init {
        itemBinding.itemContainer.setOnClickListener(this)
        itemBinding.btnLike.setOnClickListener(this)
        itemBinding.btnDislike.setOnClickListener(this)

        val context = itemBinding.root.context
        animationVoteUp = AnimationUtils.loadAnimation(context, R.anim.vote_up)
        animationVoteDown = AnimationUtils.loadAnimation(context, R.anim.vote_down)

        drawableDislike = ContextCompat.getDrawable(context, R.drawable.thumb_down_outline)!!
        drawableDislikeColored = ContextCompat.getDrawable(context, R.drawable.thumb_down_colored)!!
        drawableLike = ContextCompat.getDrawable(context, R.drawable.thumb_up_outline)!!
        drawableLikeColored = ContextCompat.getDrawable(context, R.drawable.thumb_up_colored)!!

        colorDislike = ContextCompat.getColor(context, R.color.colorDislike)
        colorLike = ContextCompat.getColor(context, R.color.colorLike)
        colorNormal = ContextCompat.getColor(context, R.color.post_button_tint)
    }

    @SuppressLint("SetTextI18n")
    fun bind(item: CommentItemEntity) {
        this.comment = item

        itemBinding.name.text = item.author?.name ?: ""
        itemBinding.content.text = item.body.fromHtml()
        itemBinding.reply.text = item.replyUserName
        itemBinding.date.text = DateUtils.formatAdverts(itemBinding.root.context, item.date, TimeUnit.SECONDS)

        val likes = (item.likes - item.dislikes)
        itemBinding.likes.text = likes.toString()
        itemBinding.likes.setTextColor(when {
             likes > 0 -> colorLike
             likes < 0 -> colorDislike
            else -> colorNormal
        })

        itemBinding.content.isGone = item.body.isEmpty()
        itemBinding.reply.isGone = item.replyUserName.isEmpty()

        if(comment.vote == 1) {
            itemBinding.btnLike.setImageDrawable(drawableLikeColored)
        } else {
            itemBinding.btnLike.setImageDrawable(drawableLike)
        }
        if(comment.vote == -1) {
            itemBinding.btnDislike.setImageDrawable(drawableDislikeColored)
        } else {
            itemBinding.btnDislike.setImageDrawable(drawableDislike)
        }

        if(item.attachments.isEmpty()) {
            itemBinding.mainAttach.isGone = true
            itemBinding.play.isGone = true
        } else {
            val attach = item.attachments.first()
            itemBinding.mainAttach.setOnClickListener {
                listener.onClickedAttach(it, attach)
            }
            itemBinding.play.isGone = attach.type != 25
            val url = if(attach.type == ATTACH_TYPE_INTERNAL_IMAGE) attach.url else attach.previewUrl
            itemBinding.mainAttach.isGone = false
            Glide.with(itemBinding.root)
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(itemBinding.mainAttach)
        }

        item.author?.profileImage?.let { url ->
            Glide.with(itemBinding.root)
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .transform(CircleCrop())
                .into(itemBinding.image)
        }
        ViewCompat.setTransitionName(itemBinding.itemContainer, item.id.toString())
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.item_container -> {
                listener.onClickedItem(v, comment)
            }
            R.id.btnLike -> {
                animationVoteUp.reset()
                v.startAnimation(animationVoteUp)
                listener.onClickedLike(v, comment)
            }
            R.id.btnDislike -> {
                animationVoteDown.reset()
                v.startAnimation(animationVoteDown)
                listener.onClickedDislike(v, comment)
            }
        }
    }
}

