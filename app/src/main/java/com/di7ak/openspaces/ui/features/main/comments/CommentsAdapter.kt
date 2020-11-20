package com.di7ak.openspaces.ui.features.main.comments

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
import com.di7ak.openspaces.ui.utils.MenuDialog
import com.di7ak.openspaces.utils.DateUtils
import com.di7ak.openspaces.utils.HtmlImageGetter
import com.di7ak.openspaces.utils.createDrawableCallback
import com.di7ak.openspaces.utils.fromHtml
import kotlinx.coroutines.CoroutineScope
import java.util.concurrent.TimeUnit

class CommentsAdapter(
    private val imageGetter: HtmlImageGetter,
    private val scope: CoroutineScope,
    private val listener: CommentsItemListener
) :
    RecyclerView.Adapter<CommentViewHolder>() {

    interface CommentsItemListener {
        fun onClickedItem(view: View, item: CommentItemEntity)
        fun onClickedLike(view: View, item: CommentItemEntity)
        fun onClickedDislike(view: View, item: CommentItemEntity)
        fun onClickedAttach(view: View, item: Attach)
        fun onClickedReply(view: View?, item: CommentItemEntity)
        fun onClickedMenuItemClick(view: View?, action: Int, item: CommentItemEntity)
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

    fun deleteItem(item: CommentItemEntity) = items.indexOf(item).apply {
        items.removeAt(this)
        if(this != -1) notifyItemRemoved(this)
    }

    fun addItem(item: CommentItemEntity) = items.size.apply {
        items.add(item)
        notifyItemInserted(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val binding: ItemCommentBinding =
            ItemCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CommentViewHolder(binding, imageGetter,scope, listener)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) =
        holder.bind(items[position])
}

class CommentViewHolder(
    private val itemBinding: ItemCommentBinding,
    private val imageGetter: HtmlImageGetter,
    private val scope: CoroutineScope,
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
    private var timePlaceholder: String
    private var replyPlaceholder: String
    private var menu: MenuDialog

    init {
        itemBinding.itemContainer.setOnClickListener(this)
        itemBinding.btnLike.setOnClickListener(this)
        itemBinding.btnDislike.setOnClickListener(this)
        itemBinding.btnReply.setOnClickListener(this)
        itemBinding.reply.setOnClickListener(this)
        itemBinding.menu.setOnClickListener(this)

        val context = itemBinding.root.context
        animationVoteUp = AnimationUtils.loadAnimation(context, R.anim.vote_up)
        animationVoteDown = AnimationUtils.loadAnimation(context, R.anim.vote_down)

        drawableDislike = ContextCompat.getDrawable(context, R.drawable.thumb_down_outline)!!
        drawableDislikeColored = ContextCompat.getDrawable(context, R.drawable.thumb_down_colored)!!
        drawableLike = ContextCompat.getDrawable(context, R.drawable.thumb_up_outline)!!
        drawableLikeColored = ContextCompat.getDrawable(context, R.drawable.thumb_up_colored)!!

        colorDislike = ContextCompat.getColor(context, R.color.colorDislike)
        colorLike = ContextCompat.getColor(context, R.color.colorLike)
        colorNormal = ContextCompat.getColor(context, R.color.buttonNormal)

        timePlaceholder = context.getString(R.string.time_holder)
        replyPlaceholder = context.getString(R.string.reply_to_placeholder)

        menu = MenuDialog(context, R.menu.comment, object : MenuDialog.MenuDialogListener{
            override fun onMenuItemClick(itemId: Int) {
                listener.onClickedMenuItemClick(itemBinding.menu, itemId, comment)
            }
        })
    }

    @SuppressLint("SetTextI18n")
    fun bind(item: CommentItemEntity) {
        this.comment = item

        itemBinding.replyBody.isGone = true

        itemBinding.name.text = item.author?.name ?: ""
        item.body.fromHtml(scope, imageGetter, {
            itemBinding.content.text = it
        }, itemBinding.content.createDrawableCallback())
        item.replyCommentText.fromHtml(scope, imageGetter, {
            itemBinding.replyBody.text = it
        }, itemBinding.replyBody.createDrawableCallback())
        itemBinding.reply.text = replyPlaceholder.format(item.replyUserName)
        itemBinding.date.text = timePlaceholder.format(DateUtils.formatAdverts(itemBinding.root.context, item.date, TimeUnit.SECONDS))

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
        val hide = mutableListOf<Int>()
        if(item.deleteLink.isEmpty()) hide.add(R.id.delete)
        if(item.editLink.isEmpty()) hide.add(R.id.edit)
        menu.hideItems(hide)

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
            R.id.btnReply -> {
                listener.onClickedReply(v, comment)
            }
            R.id.reply -> {
                itemBinding.replyBody.isGone = !itemBinding.replyBody.isGone
            }
            R.id.menu -> {
                menu.show()
            }
        }
    }
}

