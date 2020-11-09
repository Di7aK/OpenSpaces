package com.di7ak.openspaces.ui.features.lenta

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.di7ak.openspaces.R
import com.di7ak.openspaces.databinding.ItemLentaBinding
import com.di7ak.openspaces.utils.DateUtils
import com.di7ak.openspaces.utils.fromHtml

class LentaAdapter(private val listener: LentaItemListener) :
    RecyclerView.Adapter<LentaViewHolder>() {

    interface LentaItemListener {
        fun onClickedItem(view: View, item: LentaModel)
        fun onClickedLike(view: View, item: LentaModel)
        fun onClickedDislike(view: View, item: LentaModel)
    }

    private val items = ArrayList<LentaModel>()

    fun setItems(items: ArrayList<LentaModel>) {
        this.items.clear()
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LentaViewHolder {
        val binding: ItemLentaBinding =
            ItemLentaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LentaViewHolder(binding, listener)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: LentaViewHolder, position: Int) =
        holder.bind(items[position])
}

class LentaViewHolder(
    private val itemBinding: ItemLentaBinding,
    private val listener: LentaAdapter.LentaItemListener
) : RecyclerView.ViewHolder(itemBinding.root),
    View.OnClickListener {
    private lateinit var event: LentaModel

    init {
        itemBinding.itemContainer.setOnClickListener(this)
        itemBinding.btnLike.setOnClickListener(this)
        itemBinding.btnDislike.setOnClickListener(this)
    }

    @SuppressLint("SetTextI18n")
    fun bind(item: LentaModel) {
        this.event = item
        val context = itemBinding.root.context

        itemBinding.name.text = item.author?.name ?: ""
        itemBinding.title.text = item.title.fromHtml()
        itemBinding.content.text = item.body.fromHtml()
        itemBinding.likes.text = item.likes.toString()
        itemBinding.dislikes.text = item.dislikes.toString()
        itemBinding.comments.text = item.commentsCount.toString()
        itemBinding.date.text = DateUtils.formatAdverts(itemBinding.root.context, item.date)

        if(event.liked) {
            itemBinding.btnLike.setColorFilter(ContextCompat.getColor(context, R.color.colorLike))
        } else {
            itemBinding.btnLike.setColorFilter(ContextCompat.getColor(context, R.color.post_button_tint))
        }
        if(event.disliked) {
            itemBinding.btnDislike.setColorFilter(ContextCompat.getColor(context, R.color.colorDislike))
        } else {
            itemBinding.btnDislike.setColorFilter(ContextCompat.getColor(context, R.color.post_button_tint))
        }

        item.author?.profileImage?.let { url ->
            Glide.with(itemBinding.root)
                .load(url)
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
                listener.onClickedLike(v, event)
            }
            R.id.btnDislike -> {
                listener.onClickedDislike(v, event)
            }
        }
    }
}

