package com.di7ak.openspaces.ui.features.main.journal

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.isGone
import androidx.recyclerview.widget.RecyclerView
import com.di7ak.openspaces.R
import com.di7ak.openspaces.data.entities.JournalItemEntity
import com.di7ak.openspaces.databinding.ItemJournalBinding
import com.di7ak.openspaces.utils.HtmlImageGetter
import com.di7ak.openspaces.utils.createDrawableCallback
import com.di7ak.openspaces.utils.fromHtml
import kotlinx.coroutines.CoroutineScope

class JournalAdapter(
    private val imageGetter: HtmlImageGetter,
    private val scope: CoroutineScope,
    private val listener: JournalItemListener
) :
    RecyclerView.Adapter<JournalViewHolder>() {

    interface JournalItemListener {
        fun onClickedItem(view: View, item: JournalItemEntity)
    }

    private val items = mutableListOf<JournalItemEntity>()

    fun setItems(items: ArrayList<JournalItemEntity>) {
        this.items.clear()
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JournalViewHolder {
        val binding =
            ItemJournalBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return JournalViewHolder(binding, imageGetter,scope, listener)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: JournalViewHolder, position: Int) =
        holder.bind(items[position])
}

class JournalViewHolder(
    private val itemBinding: ItemJournalBinding,
    private val imageGetter: HtmlImageGetter,
    private val scope: CoroutineScope,
    private val listener: JournalAdapter.JournalItemListener
) : RecyclerView.ViewHolder(itemBinding.root),
    View.OnClickListener {
    private lateinit var record: JournalItemEntity
    private var timePlaceholder: String

    init {
        itemBinding.itemContainer.setOnClickListener(this)
        timePlaceholder = itemBinding.root.context.getString(R.string.time_holder)
    }

    @SuppressLint("SetTextI18n")
    fun bind(item: JournalItemEntity) {
        this.record = item

        itemBinding.header.text = item.header
        itemBinding.date.text = timePlaceholder.format(item.date)
        itemBinding.date.isGone = item.date.isNullOrBlank()
        item.message.fromHtml(scope, imageGetter, {
            itemBinding.message.text = it
        }, itemBinding.message.createDrawableCallback())
        item.text.fromHtml(scope, imageGetter, {
            itemBinding.text.text = it
            itemBinding.text.isGone = it.isBlank()
        }, itemBinding.text.createDrawableCallback())

        ViewCompat.setTransitionName(itemBinding.itemContainer, item.id.toString())
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.item_container -> {
                listener.onClickedItem(v, record)
            }
        }
    }
}

