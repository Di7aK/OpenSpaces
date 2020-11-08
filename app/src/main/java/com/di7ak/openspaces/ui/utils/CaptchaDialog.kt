package com.di7ak.openspaces.ui.utils

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import com.bumptech.glide.Glide
import com.di7ak.openspaces.R
import com.di7ak.openspaces.databinding.CaptchaDialogBinding
import com.google.android.material.bottomsheet.BottomSheetDialog

class CaptchaDialog(context: Context, private val listener: CaptchaListener) :
    BottomSheetDialog(context, R.style.CustomBottomSheetDialogTheme), View.OnClickListener {
    private val binding: CaptchaDialogBinding = CaptchaDialogBinding.inflate(LayoutInflater.from(context))

    var captchaUrl: String = ""
        set(value) {
            Glide.with(binding.captchaImage)
                .load(value)
                .into(binding.captchaImage)
            show()
        }

    init {
        setContentView(binding.root)

        binding.btnSend.setOnClickListener(this)
        binding.btnCancel.setOnClickListener(this)

        binding.code.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                binding.btnSend.performClick()
            }
            true
        }
    }

    override fun onClick(v: View?) {
        if (v?.id == R.id.btnCancel) hide()
        else if (v?.id == R.id.btnSend) {
            listener.onEnter(binding.code.text.toString())
            hide()
        }
    }

    interface CaptchaListener {
        fun onEnter(code: String)
    }
}