package com.di7ak.openspaces.ui.utils

import android.content.Context
import android.view.LayoutInflater
import androidx.annotation.MenuRes
import androidx.core.view.forEach
import com.di7ak.openspaces.R
import com.di7ak.openspaces.databinding.DialogMenuBinding
import com.google.android.material.bottomsheet.BottomSheetDialog

class MenuDialog(
    context: Context,
    @MenuRes
    private val menuRes: Int = 0,
    private val listener: MenuDialogListener? = null
) : BottomSheetDialog(context, R.style.CustomBottomSheetDialogTheme) {
    private val binding = DialogMenuBinding.inflate(LayoutInflater.from(context))

    fun hideItems(itemsId: List<Int>) {
        binding.menu.menu.forEach {
            it.isVisible = !itemsId.contains(it.itemId)
        }
    }

    init {
        setContentView(binding.root)

        binding.menu.inflateMenu(menuRes)

        binding.menu.setNavigationItemSelectedListener {
            listener?.onMenuItemClick(it.itemId)
            hide()
            true
        }
    }

    interface MenuDialogListener {
        fun onMenuItemClick(itemId: Int)
    }
}