package com.di7ak.openspaces.ui.utils

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.view.isGone
import com.di7ak.openspaces.R
import com.di7ak.openspaces.databinding.ConfirmDialogBinding
import com.google.android.material.bottomsheet.BottomSheetDialog


class ConfirmDialog(context: Context,
                    @DrawableRes private val icon: Int? = null,
                    @StringRes private val title: Int? = null,
                    @StringRes private val subtitle: Int? = null,
                    @StringRes private val confirmText: Int? = null,
                    @StringRes private val cancelText: Int? = null,
                    private val listener: ConfirmDialogListener? = null) :
    BottomSheetDialog(context, R.style.CustomBottomSheetDialogTheme), View.OnClickListener {
    private val binding: ConfirmDialogBinding = ConfirmDialogBinding.inflate(LayoutInflater.from(context))

    init {
        setContentView(binding.root)

        binding.btnConfirm.setOnClickListener(this)
        binding.btnCancel.setOnClickListener(this)

        if(icon == null) {
            binding.icon.isGone = true
        } else {
            binding.icon.setImageResource(icon)
        }
        if(title != null) binding.title.setText(title)
        if(subtitle != null) binding.subtitle.setText(subtitle)
        if(confirmText != null) binding.btnConfirm.setText(confirmText)
        if(cancelText != null) binding.btnCancel.setText(cancelText)
    }

    override fun onClick(v: View?) {
        if (v?.id == R.id.btnCancel) {
            hide()
        } else if (v?.id == R.id.btnConfirm) {
            listener?.onAccept()
            hide()
        }
    }

    interface ConfirmDialogListener {
        fun onAccept()
    }
}