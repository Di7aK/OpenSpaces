package com.di7ak.openspaces.ui.features.auth.accounts

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.isGone
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.di7ak.openspaces.R
import com.di7ak.openspaces.data.entities.AuthAttributes
import com.di7ak.openspaces.databinding.ItemAccountBinding

class AccountsAdapter(private val listener: AccountsItemListener) : RecyclerView.Adapter<AccountViewHolder>() {
    interface AccountsItemListener {
        fun onClickedSession(view: View, session: AuthAttributes)

        fun onClickedDelete(session: AuthAttributes)
    }

    private val items = ArrayList<AuthAttributes>()

    fun setItems(items: ArrayList<AuthAttributes>) {
        this.items.clear()
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    fun updateItem(item: AuthAttributes) = items.indexOf(item).apply {
        if (this != -1) notifyItemChanged(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccountViewHolder {
        val binding: ItemAccountBinding = ItemAccountBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AccountViewHolder(binding, listener)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: AccountViewHolder, position: Int) = holder.bind(items[position])
}

class AccountViewHolder(private val itemBinding: ItemAccountBinding, private val listener: AccountsAdapter.AccountsItemListener) : RecyclerView.ViewHolder(itemBinding.root),
    View.OnClickListener {

    private lateinit var authAttributes: AuthAttributes

    init {
        itemBinding.itemContainer.setOnClickListener(this)
        itemBinding.btnDelete.setOnClickListener(this)
    }

    @SuppressLint("SetTextI18n")
    fun bind(item: AuthAttributes) {
        this.authAttributes = item
        itemBinding.name.text = item.name
        itemBinding.speciesAndStatus.text = """${item.userId}"""

        itemBinding.btnDelete.isGone = item.progress
        itemBinding.progress.isGone = !item.progress

        Glide.with(itemBinding.root)
            .load(item.avatar)
            .transform(CircleCrop())
            .into(itemBinding.image)
        ViewCompat.setTransitionName(itemBinding.itemContainer, item.userId.toString())
    }

    override fun onClick(v: View) {
        if(v.id == R.id.btnDelete) {
            listener.onClickedDelete(authAttributes)
        } else {
            listener.onClickedSession(v, authAttributes)
        }
    }
}

