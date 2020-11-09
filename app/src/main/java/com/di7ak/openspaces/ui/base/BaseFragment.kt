package com.di7ak.openspaces.ui.base

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.di7ak.openspaces.ui.utils.CaptchaDialog
import com.google.android.material.transition.MaterialContainerTransform

open class BaseFragment : Fragment(), CaptchaDialog.CaptchaListener {
    private val captchaDialog: CaptchaDialog by lazy { CaptchaDialog(requireContext(), this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        sharedElementEnterTransition = MaterialContainerTransform().apply {
            duration = 400L
            isElevationShadowEnabled = true
            setAllContainerColors(Color.WHITE)
        }
        super.onCreate(savedInstanceState)
    }

    fun showCaptcha(url: String) {
        captchaDialog.captchaUrl = url
    }

    override fun onCaptchaEntered(code: String) {

    }
}