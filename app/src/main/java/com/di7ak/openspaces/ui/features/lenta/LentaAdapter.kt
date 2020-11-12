package com.di7ak.openspaces.ui.features.lenta

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.*
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.isGone
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.di7ak.openspaces.R
import com.di7ak.openspaces.data.entities.Attach
import com.di7ak.openspaces.data.entities.LentaItemEntity
import com.di7ak.openspaces.databinding.ItemLentaBinding
import com.di7ak.openspaces.databinding.ItemLentaImageBinding
import com.di7ak.openspaces.databinding.ItemLentaWithImageBinding
import com.di7ak.openspaces.utils.DateUtils
import com.di7ak.openspaces.utils.HtmlImageGetter
import com.di7ak.openspaces.utils.createDrawableCallback
import com.di7ak.openspaces.utils.fromHtml
import kotlinx.coroutines.CoroutineScope
import java.util.concurrent.TimeUnit

class LentaAdapter(
    private val imageGetter: HtmlImageGetter,
    private val scope: CoroutineScope,
    private val listener: LentaItemListener
) :
    RecyclerView.Adapter<LentaViewHolder<ViewBinding>>() {

    companion object {
        private const val VIEW_TYPE_POST = 0
        private const val VIEW_TYPE_POST_WITH_IMAGE = 1
        private const val VIEW_TYPE_POST_IMAGE = 2
    }

    interface LentaItemListener {
        fun onClickedItem(view: View, item: LentaItemEntity)
        fun onClickedLike(view: View, item: LentaItemEntity)
        fun onClickedDislike(view: View, item: LentaItemEntity)
        fun onClickedAttach(view: View, item: Attach)
    }

    private val items = mutableListOf<LentaItemEntity>()

    fun setItems(items: ArrayList<LentaItemEntity>) {
        this.items.clear()
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    fun updateItem(item: LentaItemEntity) = items.indexOf(item).apply {
        if (this != -1) notifyItemChanged(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LentaViewHolder<ViewBinding> {
        return when (viewType) {
            VIEW_TYPE_POST_WITH_IMAGE -> {
                val binding: ItemLentaWithImageBinding =
                    ItemLentaWithImageBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                LentaViewHolder(binding, imageGetter, scope, listener)
            }
            VIEW_TYPE_POST_IMAGE -> {
                val binding: ItemLentaImageBinding =
                    ItemLentaImageBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                LentaViewHolder(binding, imageGetter, scope, listener)
            }
            else -> {
                val binding: ItemLentaBinding =
                    ItemLentaBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                LentaViewHolder(binding, imageGetter, scope, listener)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        val item = items[position]
        return if(item.attachments.isNotEmpty() && item.body.isEmpty()) {
            VIEW_TYPE_POST_IMAGE
        } else if(item.attachments.isNotEmpty() && item.body.isNotEmpty()) {
            VIEW_TYPE_POST_WITH_IMAGE
        } else if(item.attachments.isEmpty()) {
            VIEW_TYPE_POST
        } else return super.getItemViewType(position)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: LentaViewHolder<ViewBinding>, position: Int) =
        holder.bind(items[position])
}

//чото тут придумать надо чтобы не дублить эту хуйню
//чото тут придумать надо чтобы не дублить эту хуйню
//чото тут придумать надо чтобы не дублить эту хуйню
class LentaViewHolder<T : ViewBinding>(
    private val itemBinding: T,
    private val imageGetter: HtmlImageGetter,
    private val scope: CoroutineScope,
    private val listener: LentaAdapter.LentaItemListener
) : RecyclerView.ViewHolder(itemBinding.root),
    View.OnClickListener {
    private lateinit var event: LentaItemEntity
    private var animationVoteUp: Animation
    private var animationVoteDown: Animation
    private var drawableLikeColored: Drawable
    private var drawableDislikeColored: Drawable
    private var drawableLike: Drawable
    private var drawableDislike: Drawable
    private var timePlaceholder: String

    init {
        if(itemBinding is ItemLentaBinding) {
            itemBinding.itemContainer.setOnClickListener(this)
            itemBinding.btnLike.setOnClickListener(this)
            itemBinding.btnDislike.setOnClickListener(this)
            itemBinding.btnComments.setOnClickListener(this)
        } else if(itemBinding is ItemLentaImageBinding) {
            itemBinding.itemContainer.setOnClickListener(this)
            itemBinding.btnLike.setOnClickListener(this)
            itemBinding.btnDislike.setOnClickListener(this)
            itemBinding.btnComments.setOnClickListener(this)
        } else if(itemBinding is ItemLentaWithImageBinding) {
            itemBinding.itemContainer.setOnClickListener(this)
            itemBinding.btnLike.setOnClickListener(this)
            itemBinding.btnDislike.setOnClickListener(this)
            itemBinding.btnComments.setOnClickListener(this)
        }

        val context = itemBinding.root.context
        animationVoteUp = AnimationUtils.loadAnimation(context, R.anim.vote_up)
        animationVoteDown = AnimationUtils.loadAnimation(context, R.anim.vote_down)

        drawableDislike = ContextCompat.getDrawable(context, R.drawable.thumb_down_outline)!!
        drawableDislikeColored = ContextCompat.getDrawable(context, R.drawable.thumb_down_colored)!!
        drawableLike = ContextCompat.getDrawable(context, R.drawable.thumb_up_outline)!!
        drawableLikeColored = ContextCompat.getDrawable(context, R.drawable.thumb_up_colored)!!

        timePlaceholder = context.getString(R.string.time_holder)
    }

    @SuppressLint("SetTextI18n")
    fun bind(item: LentaItemEntity) {
        this.event = item

        if(itemBinding is ItemLentaBinding) {
            itemBinding.name.text = item.author?.name ?: ""
            item.title.fromHtml(scope, imageGetter, {
                itemBinding.title.text = it
            }, itemBinding.title.createDrawableCallback())
            item.body.fromHtml(scope, imageGetter, {
                itemBinding.content.text = it
            }, itemBinding.content.createDrawableCallback())
            itemBinding.likes.text = item.likes.toString()
            itemBinding.dislikes.text = item.dislikes.toString()
            itemBinding.comments.text = item.commentsCount.toString()
            itemBinding.date.text =
                timePlaceholder.format(
                    DateUtils.formatAdverts(
                        itemBinding.root.context,
                        item.date,
                        TimeUnit.SECONDS
                    )
                )

            itemBinding.content.isGone = item.body.isEmpty()

            if (event.liked) {
                itemBinding.btnLike.setImageDrawable(drawableLikeColored)
            } else {
                itemBinding.btnLike.setImageDrawable(drawableLike)
            }
            if (event.disliked) {
                itemBinding.btnDislike.setImageDrawable(drawableDislikeColored)
            } else {
                itemBinding.btnDislike.setImageDrawable(drawableDislike)
            }

            if (item.attachments.isEmpty()) {
                itemBinding.mainAttach.isGone = true
                itemBinding.play.isGone = true
            } else {
                val attach = item.attachments.first()
                itemBinding.mainAttach.setOnClickListener {
                    listener.onClickedAttach(it, attach)
                }
                itemBinding.play.isGone = attach.type != 25
                itemBinding.mainAttach.isGone = false
                Glide.with(itemBinding.root)
                    .load(attach.previewUrl)
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
        } else if(itemBinding is ItemLentaWithImageBinding) {
            itemBinding.name.text = item.author?.name ?: ""
            item.title.fromHtml(scope, imageGetter, {
                itemBinding.title.text = it
            }, itemBinding.title.createDrawableCallback())
            item.body.fromHtml(scope, imageGetter, {
                itemBinding.content.text = it
            }, itemBinding.content.createDrawableCallback())
            itemBinding.likes.text = item.likes.toString()
            itemBinding.dislikes.text = item.dislikes.toString()
            itemBinding.comments.text = item.commentsCount.toString()
            itemBinding.date.text =
                timePlaceholder.format(
                    DateUtils.formatAdverts(
                        itemBinding.root.context,
                        item.date,
                        TimeUnit.SECONDS
                    )
                )

            itemBinding.content.isGone = item.body.isEmpty()

            if (event.liked) {
                itemBinding.btnLike.setImageDrawable(drawableLikeColored)
            } else {
                itemBinding.btnLike.setImageDrawable(drawableLike)
            }
            if (event.disliked) {
                itemBinding.btnDislike.setImageDrawable(drawableDislikeColored)
            } else {
                itemBinding.btnDislike.setImageDrawable(drawableDislike)
            }

            if (item.attachments.isEmpty()) {
                itemBinding.mainAttach.isGone = true
                itemBinding.play.isGone = true
            } else {
                val attach = item.attachments.first()
                itemBinding.mainAttach.setOnClickListener {
                    listener.onClickedAttach(it, attach)
                }
                itemBinding.play.isGone = attach.type != 25
                itemBinding.mainAttach.isGone = false
                Glide.with(itemBinding.root)
                    .load(attach.previewUrl)
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
        } else {
            if(itemBinding is ItemLentaImageBinding) {
                itemBinding.name.text = item.author?.name ?: ""
                item.title.fromHtml(scope, imageGetter, {
                    itemBinding.title.text = it
                }, itemBinding.title.createDrawableCallback())
                item.body.fromHtml(scope, imageGetter, {
                    itemBinding.content.text = it
                }, itemBinding.content.createDrawableCallback())
                itemBinding.likes.text = item.likes.toString()
                itemBinding.dislikes.text = item.dislikes.toString()
                itemBinding.comments.text = item.commentsCount.toString()
                itemBinding.date.text =
                    timePlaceholder.format(
                        DateUtils.formatAdverts(
                            itemBinding.root.context,
                            item.date,
                            TimeUnit.SECONDS
                        )
                    )

                itemBinding.content.isGone = item.body.isEmpty()

                if (event.liked) {
                    itemBinding.btnLike.setImageDrawable(drawableLikeColored)
                } else {
                    itemBinding.btnLike.setImageDrawable(drawableLike)
                }
                if (event.disliked) {
                    itemBinding.btnDislike.setImageDrawable(drawableDislikeColored)
                } else {
                    itemBinding.btnDislike.setImageDrawable(drawableDislike)
                }

                if (item.attachments.isEmpty()) {
                    itemBinding.mainAttach.isGone = true
                    itemBinding.play.isGone = true
                } else {
                    val attach = item.attachments.first()
                    itemBinding.mainAttach.setOnClickListener {
                        listener.onClickedAttach(it, attach)
                    }
                    itemBinding.play.isGone = attach.type != 25
                    itemBinding.mainAttach.isGone = false
                    Glide.with(itemBinding.root)
                        .load(attach.previewUrl)
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
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.item_container, R.id.btnComments -> {
                listener.onClickedItem(v, event)
            }
            R.id.btnLike -> {
                animationVoteUp.reset()
                v.startAnimation(animationVoteUp)
                listener.onClickedLike(v, event)
            }
            R.id.btnDislike -> {
                animationVoteDown.reset()
                v.startAnimation(animationVoteDown)
                listener.onClickedDislike(v, event)
            }
        }
    }
}

