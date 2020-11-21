package com.di7ak.openspaces.ui.utils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.di7ak.openspaces.databinding.ItemLoadEmptyBinding
import com.di7ak.openspaces.databinding.ItemLoadErrorBinding
import com.di7ak.openspaces.databinding.ItemLoadProgressBinding

class ProgressAdapter(private val retryCallback: () -> Unit) :
    RecyclerView.Adapter<ProgressAdapter.LoadViewHolder>() {
    companion object {
        private const val VIEW_TYPE_ERROR = 0
        private const val VIEW_TYPE_PROGRESS = 1
        private const val VIEW_TYPE_EMPTY = 2
        private const val VIEW_TYPE_NOTHING = 3
    }

    var isEmpty: Boolean = false
        set(value) {
            field = value
            isError = false
        }
    var isError: Boolean = false
        set(value) {
            field = value
            isProgress = false
        }
    var isProgress: Boolean = false
        set(value) {
            field = value
            notifyItemChanged(0)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LoadViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_PROGRESS -> {
                LoadProgressViewHolder(ItemLoadProgressBinding.inflate(inflater, parent, false))
            }
            VIEW_TYPE_ERROR -> LoadErrorViewHolder(
                ItemLoadErrorBinding.inflate(inflater, parent, false),
                retryCallback
            )
            VIEW_TYPE_EMPTY -> ItemLoadEmptyViewHolder(
                ItemLoadEmptyBinding.inflate(inflater, parent, false)
            )
            else -> LoadViewHolder(View(parent.context))
        }
    }

    override fun onBindViewHolder(holder: LoadViewHolder, position: Int) {}

    override fun getItemViewType(position: Int): Int {
        return when {
            isError -> VIEW_TYPE_ERROR
            isProgress -> VIEW_TYPE_PROGRESS
            isEmpty -> VIEW_TYPE_EMPTY
            else -> VIEW_TYPE_NOTHING
        }
    }

    override fun getItemCount() = 1

    open class LoadViewHolder(view: View) : RecyclerView.ViewHolder(view)

    class LoadProgressViewHolder(binding: ItemLoadProgressBinding) : LoadViewHolder(binding.root)
    class ItemLoadEmptyViewHolder(binding: ItemLoadEmptyBinding) : LoadViewHolder(binding.root)

    class LoadErrorViewHolder(
        binding: ItemLoadErrorBinding,
        private val retryCallback: () -> Unit
    ) : LoadViewHolder(binding.root) {
        init {
            binding.btnRetry.setOnClickListener {
                retryCallback()
            }
        }
    }
}