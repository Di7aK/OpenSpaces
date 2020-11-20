package com.di7ak.openspaces.ui.base

import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.di7ak.openspaces.R
import com.di7ak.openspaces.ui.features.main.MainActivity
import com.di7ak.openspaces.ui.utils.CaptchaDialog
import com.di7ak.openspaces.utils.getColorFromAttr
import com.google.android.material.transition.MaterialContainerTransform

open class BaseFragment : Fragment(), CaptchaDialog.CaptchaListener {
    private val captchaDialog: CaptchaDialog by lazy { CaptchaDialog(requireContext(), this) }
    private var showNavigation: Boolean = false
    private val backPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            onBackPressed()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        sharedElementEnterTransition = MaterialContainerTransform().apply {
            duration = 400L
            isElevationShadowEnabled = true

            setAllContainerColors(requireContext().getColorFromAttr(R.attr.colorContainer))
        }

        super.onCreate(savedInstanceState)
    }

    fun setHasNavigationMenu(showNavigation: Boolean) {
        this.showNavigation = showNavigation
    }

    override fun onPause() {
        super.onPause()

        if(!showNavigation) showNavigation()
    }

    fun showCaptcha(url: String) {
        captchaDialog.captchaUrl = url
    }

    override fun onCaptchaEntered(code: String) {}

    fun setResult(resultCode: Int, intent: Intent? = null) {
        activity?.apply {
            setResult(resultCode, intent)
            finish()
        }
    }

    open fun onBackPressed() {
        if(!findNavController().popBackStack()) activity?.finish()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        (activity as AppCompatActivity).onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            backPressedCallback
        )
    }

    fun hideNavigation(callback: () -> Unit = {}) {
        if(activity is MainActivity) {
            (activity as MainActivity?)?.hideNavigation(callback)
        }
    }

    fun showNavigation(callback: () -> Unit = {}) {
        if(activity is MainActivity) {
            (activity as MainActivity?)?.showNavigation(callback)
        }
    }
}