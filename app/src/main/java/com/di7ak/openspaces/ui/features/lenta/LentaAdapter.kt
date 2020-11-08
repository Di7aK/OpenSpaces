package com.di7ak.openspaces.ui.features.lenta

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.di7ak.openspaces.R
import com.di7ak.openspaces.data.entities.lenta.Events
import com.di7ak.openspaces.databinding.ItemLentaBinding
import com.di7ak.openspaces.utils.fromHtml

class LentaAdapter(private val listener: LentaItemListener) : RecyclerView.Adapter<LentaViewHolder>() {

    interface LentaItemListener {
        fun onClickedItem(view: View, session: Events)
    }

    private val items = ArrayList<Events>()

    fun setItems(items: ArrayList<Events>) {
        this.items.clear()
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LentaViewHolder {
        val binding: ItemLentaBinding = ItemLentaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LentaViewHolder(binding, listener)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: LentaViewHolder, position: Int) = holder.bind(items[position])
}

class LentaViewHolder(private val itemBinding: ItemLentaBinding, private val listener: LentaAdapter.LentaItemListener) : RecyclerView.ViewHolder(itemBinding.root),
    View.OnClickListener {

    private lateinit var authAttributes: Events

    init {
        itemBinding.itemContainer.setOnClickListener(this)
    }

    @SuppressLint("SetTextI18n")
    fun bind(item: Events) {
        this.authAttributes = item
        val detail = item.items.first()
        itemBinding.name.text = detail.userWidget?.name ?: ""
        itemBinding.speciesAndStatus.text = detail.subject.fromHtml()
        itemBinding.content.text = detail.description.fromHtml()
        Glide.with(itemBinding.root)
            .load(item.author_avatar.previewURL)
            .transform(CircleCrop())
            .into(itemBinding.image)
        ViewCompat.setTransitionName(itemBinding.itemContainer, item.id)
    }

    override fun onClick(v: View) {
        if(v.id == R.id.item_container) {
            listener.onClickedItem(v, authAttributes)
        }
    }
}

