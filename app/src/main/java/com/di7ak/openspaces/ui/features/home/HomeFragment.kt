package com.di7ak.openspaces.ui.features.home

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
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
    }

    fun hideNavigation() {
        binding.navigation.isGone = true
    }

    fun showNavigation() {
        binding.navigation.isGone = false
    }
}
