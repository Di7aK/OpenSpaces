package com.di7ak.openspaces.ui.utils

import android.content.Context
import android.view.LayoutInflater
import androidx.annotation.MenuRes
import com.di7ak.openspaces.R
import com.di7ak.openspaces.databinding.MenuDialogBinding
import com.google.android.material.bottomsheet.BottomSheetDialog

class MenuDialog(context: Context, private val listener: MenuDialogListener? = null) :
    BottomSheetDialog(context, R.style.CustomBottomSheetDialogTheme) {
    private val binding: MenuDialogBinding = MenuDialogBinding.inflate(LayoutInflater.from(context))

    @MenuRes
    var menuRes: Int = 0
    set(value) {
        field = value
        binding.menu.menu.clear()
        binding.menu.inflateMenu(value)
    }
    init {
        setContentView(binding.root)

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