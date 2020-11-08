package com.di7ak.openspaces.ui.features.web

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.core.view.doOnPreDraw
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.di7ak.openspaces.data.*
import com.di7ak.openspaces.databinding.WebFragmentBinding
import com.di7ak.openspaces.utils.Resource
import com.di7ak.openspaces.utils.autoCleared
import com.google.android.material.transition.MaterialContainerTransform
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WebFragment : Fragment() {
    private var binding: WebFragmentBinding by autoCleared()
    private val viewModel: WebViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {

        sharedElementEnterTransition = MaterialContainerTransform().apply {
            duration = 500L
            isElevationShadowEnabled = true
            setAllContainerColors(Color.WHITE)
        }
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = WebFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupWebView()

        setObservers()
        val userId = requireArguments().getInt("userId")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            binding.detailContainer.transitionName = userId.toString()
        }

        viewModel.setUserId(userId)
    }

    private fun setObservers() {
        var checked = false
        viewModel.session.observe(viewLifecycleOwner, Observer {
            if(checked) return@Observer
            checked = true
            val cookieString = "sid=${it.sid}; path=/"
            CookieManager.getInstance().setCookie("https://spaces.im", cookieString)
            viewModel.check(it.sid)
        })

        viewModel.auth.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Resource.Status.SUCCESS -> {
                    binding.progress.isGone = true
                    binding.webView.isGone = false
                    when(it.data?.code) {
                        CODE_SUCCESS -> {
                            binding.webView.loadUrl(it.data.attributes!!.mysiteLink)
                        }
                        else -> {
                            findNavController().popBackStack()
                        }
                    }
                }

                Resource.Status.ERROR -> {
                    binding.progress.isGone = true
                    binding.webView.isGone = false
                    Toast.makeText(activity, it.message, Toast.LENGTH_SHORT).show()
                }

                Resource.Status.LOADING -> {
                    binding.progress.isGone = false
                    binding.webView.isGone = true
                }
            }
        })
    }

    @SuppressLint("SetJavaScriptEnabled")
    fun setupWebView() {
        binding.webView.apply {
            webViewClient = WebViewClient()
            webChromeClient = WebChromeClient()
            settings.javaScriptEnabled = true
            settings.allowFileAccess = true
            if (Build.VERSION.SDK_INT >= 21) {
                settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            }
        }
    }
}