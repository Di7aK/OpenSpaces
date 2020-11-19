package com.di7ak.openspaces.ui.features.home

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnticipateInterpolator
import android.view.animation.OvershootInterpolator
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.di7ak.openspaces.R
import com.di7ak.openspaces.databinding.HomeFragmentBinding
import com.di7ak.openspaces.ui.base.BaseFragment
import com.di7ak.openspaces.utils.autoCleared
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : BaseFragment() {
    companion object {
        const val EXTRA_USER_ID = "userId"
    }
    private var binding: HomeFragmentBinding by autoCleared()
    private val viewModel: HomeViewModel by viewModels()

    var progress: Float = 0f
        set(value) {
            field = value
            binding.detailContainer.progress = value
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = HomeFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userId = arguments?.getInt(EXTRA_USER_ID)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            binding.detailContainer.transitionName = userId.toString()
        }

        val controller = binding.fragment.findNavController()

        binding.navigation.setupWithNavController(controller)

        binding.navigation.getOrCreateBadge(R.id.navigation_mail).apply {
            number = 2
            badgeTextColor = ContextCompat.getColor(requireContext(), R.color.colorBadgeText)
            backgroundColor = ContextCompat.getColor(requireContext(), R.color.colorBadge)
        }
    }

    fun hideNavigation(callback: () -> Unit = {}) {
        val translateTo = binding.navigationCard.height * 2f
        binding.navigationCard.animate()
            .translationY(translateTo)
            .setDuration(350)
            .setInterpolator(AnticipateInterpolator(2f))
            .withEndAction { callback() }
            .start()
    }

    fun showNavigation(callback: () -> Unit = {}) {
        binding.navigationCard.animate()
            .translationY(0f)
            .setDuration(450)
            .setInterpolator(OvershootInterpolator(2f))
            .withEndAction { callback() }
            .start()
    }
}
