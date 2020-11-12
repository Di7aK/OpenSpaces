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
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.di7ak.openspaces.R
import com.di7ak.openspaces.data.entities.Attach
import com.di7ak.openspaces.data.entities.LentaItemEntity
import com.di7ak.openspaces.databinding.ItemLentaBinding
import com.di7ak.openspaces.utils.DateUtils
import com.di7ak.openspaces.utils.HtmlImageGetter
import com.di7ak.openspaces.utils.fromHtml
import kotlinx.coroutines.CoroutineScope
import java.util.concurrent.TimeUnit

class LentaAdapter(
    private val imageGetter: HtmlImageGetter,
    private val scope: CoroutineScope,
    private val listener: LentaItemListener
) :
    RecyclerView.Adapter<LentaViewHolder>() {

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LentaViewHolder {
        val binding: ItemLentaBinding =
            ItemLentaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LentaViewHolder(binding, imageGetter, scope, listener)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: LentaViewHolder, position: Int) =
        holder.bind(items[position])
}

class LentaViewHolder(
    private val itemBinding: ItemLentaBinding,
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
    }

    @SuppressLint("SetTextI18n")
    fun bind(item: LentaItemEntity) {
        this.event = item

        itemBinding.name.text = item.author?.name ?: ""
        item.title.fromHtml(scope, imageGetter) {
            itemBinding.title.text =it
        }
        item.body.fromHtml(scope, imageGetter) {
            itemBinding.content.text = it
        }
        itemBinding.likes.text = item.likes.toString()
        itemBinding.dislikes.text = item.dislikes.toString()
        itemBinding.comments.text = item.commentsCount.toString()
        itemBinding.date.text =
            DateUtils.formatAdverts(itemBinding.root.context, item.date, TimeUnit.SECONDS)

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

    override fun onClick(v: View) {
        when (v.id) {
            R.id.item_container -> {
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

