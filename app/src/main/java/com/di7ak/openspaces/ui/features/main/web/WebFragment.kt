package com.di7ak.openspaces.ui.features.main.web

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import androidx.core.view.isGone
import androidx.fragment.app.viewModels
import com.di7ak.openspaces.databinding.WebFragmentBinding
import com.di7ak.openspaces.ui.base.BaseFragment
import com.di7ak.openspaces.utils.autoCleared
import com.google.android.material.transition.MaterialContainerTransform
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class WebFragment : BaseFragment() {
    companion object {
        const val EXTRA_PATH = "path"
    }

    private var binding: WebFragmentBinding by autoCleared()
    private val viewModel: WebViewModel by viewModels()

    @Inject
    lateinit var remoteConfig: FirebaseRemoteConfig
    private var path = ""

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

        path = arguments?.getString(EXTRA_PATH) ?: ""

        setupWebView()
        setSession()
    }

    private fun setSession() {
        val session = viewModel.session

        val baseUrl = remoteConfig.getString("base_url")
        val cookieString = "sid=${session.current?.sid}; path=/"
        CookieManager.getInstance().setCookie(baseUrl, cookieString)

        binding.webView.loadUrl("$baseUrl/$path")
    }

    @SuppressLint("SetJavaScriptEnabled")
    fun setupWebView() {
        binding.webView.apply {
            webViewClient = object: WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)

                    binding.progress.isGone = true
                }

                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    super.onPageStarted(view, url, favicon)

                    binding.progress.isGone = false
                }
            }
            webChromeClient = WebChromeClient()
            settings.javaScriptEnabled = true
            settings.allowFileAccess = true
            if (Build.VERSION.SDK_INT >= 21) {
                settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            }
        }
    }
}