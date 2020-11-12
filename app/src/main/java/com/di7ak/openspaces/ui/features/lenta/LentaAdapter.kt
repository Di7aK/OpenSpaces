package com.di7ak.openspaces.ui.features.lenta

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.*
import android.widget.ImageView
import android.widget.TextView
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
    RecyclerView.Adapter<LentaViewHolder>() {

    companion object {
        private const val VIEW_TYPE_POST = 0
        private const val VIEW_TYPE_POST_WITH_IMAGE = 1
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LentaViewHolder {
        val layoutRes = when (viewType) {
            VIEW_TYPE_POST_WITH_IMAGE -> R.layout.item_lenta_with_image
            else -> R.layout.item_lenta
        }
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(layoutRes, parent, false)
        return LentaViewHolder(view, imageGetter, scope, listener)
    }

    override fun getItemViewType(position: Int): Int {
        val item = items[position]
        return when {
            item.attachments.isNotEmpty() -> {
                VIEW_TYPE_POST_WITH_IMAGE
            }
            item.attachments.isEmpty() -> {
                VIEW_TYPE_POST
            }
            else -> return super.getItemViewType(position)
        }
    }

    fun isEmpty() = itemCount == 0

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: LentaViewHolder, position: Int) =
        holder.bind(items[position])
}

class LentaViewHolder(
    private val view: View,
    private val imageGetter: HtmlImageGetter,
    private val scope: CoroutineScope,
    private val listener: LentaAdapter.LentaItemListener
) : RecyclerView.ViewHolder(view),
    View.OnClickListener {
    private lateinit var event: LentaItemEntity
    private var animationVoteUp: Animation
    private var animationVoteDown: Animation
    private var drawableLikeColored: Drawable
    private var drawableDislikeColored: Drawable
    private var drawableLike: Drawable
    private var drawableDislike: Drawable
    private var timePlaceholder: String

    private val name = view.findViewById<TextView>(R.id.name)!!
    private val title = view.findViewById<TextView>(R.id.title)!!
    private val content = view.findViewById<TextView>(R.id.content)!!
    private val likes = view.findViewById<TextView>(R.id.likes)!!
    private val dislikes = view.findViewById<TextView>(R.id.dislikes)!!
    private val comments = view.findViewById<TextView>(R.id.comments)!!
    private val date = view.findViewById<TextView>(R.id.date)!!
    private val btnLike = view.findViewById<ImageView>(R.id.btnLike)!!
    private val btnDislike = view.findViewById<ImageView>(R.id.btnDislike)!!
    private val mainAttach = view.findViewById<ImageView>(R.id.mainAttach)!!
    private val play = view.findViewById<ImageView>(R.id.play)!!
    private val image = view.findViewById<ImageView>(R.id.image)!!
    private val itemContainer = view.findViewById<View>(R.id.item_container)!!

    init {
        view.findViewById<View>(R.id.item_container).setOnClickListener(this)
        view.findViewById<View>(R.id.btnLike).setOnClickListener(this)
        view.findViewById<View>(R.id.btnDislike).setOnClickListener(this)
        view.findViewById<View>(R.id.btnComments).setOnClickListener(this)

        val context = view.context
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

        name.text = item.author?.name ?: ""
        item.title.fromHtml(scope, imageGetter, {
            title.text = it
        }, title.createDrawableCallback())
        item.body.fromHtml(scope, imageGetter, {
            content.text = it
        }, content.createDrawableCallback())
        likes.text = item.likes.toString()
        dislikes.text = item.dislikes.toString()
        comments.text = item.commentsCount.toString()
        date.text =
            timePlaceholder.format(
                DateUtils.formatAdverts(
                    view.context,
                    item.date,
                    TimeUnit.SECONDS
                )
            )

        content.isGone = item.body.isEmpty()

        if (event.liked) {
            btnLike.setImageDrawable(drawableLikeColored)
        } else {
            btnLike.setImageDrawable(drawableLike)
        }
        if (event.disliked) {
            btnDislike.setImageDrawable(drawableDislikeColored)
        } else {
            btnDislike.setImageDrawable(drawableDislike)
        }

        if (item.attachments.isEmpty()) {
            mainAttach.isGone = true
            play.isGone = true
        } else {
            val attach = item.attachments.first()
            mainAttach.setOnClickListener {
                listener.onClickedAttach(it, attach)
            }
            play.isGone = attach.type != 25
            mainAttach.isGone = false
            Glide.with(view)
                .load(attach.previewUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(mainAttach)
        }

        item.author?.profileImage?.let { url ->
            Glide.with(view)
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .transform(CircleCrop())
                .into(image)
        }
        ViewCompat.setTransitionName(itemContainer, item.id.toString())
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

