package com.di7ak.openspaces.ui.base

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.di7ak.openspaces.ui.MainActivity
import com.di7ak.openspaces.ui.features.home.HomeFragment

open class BaseSubFragment() : BaseFragment() {
    private var showNavigation: Boolean = false
    private var navHostFragment: NavHostFragment? = null

    private val backPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            onBackPressed()
        }
    }

    open fun onBackPressed() {
        if(!findNavController().popBackStack()) {
            (activity as MainActivity?)?.navController?.popBackStack()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if(parentFragment is NavHostFragment) {
            navHostFragment = (parentFragment as NavHostFragment)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        (activity as AppCompatActivity).onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            backPressedCallback
        )
    }

    fun setHasNavigationMenu(showNavigation: Boolean) {
        this.showNavigation = showNavigation
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //if(!showNavigation) hideNavigation()
    }

    override fun onPause() {
        super.onPause()

        if(!showNavigation) showNavigation()
    }
    fun hideNavigation(callback: () -> Unit = {}) {
        if(navHostFragment?.parentFragment is HomeFragment) {
            (navHostFragment?.parentFragment as HomeFragment?)?.hideNavigation(callback)
        }
    }

    fun showNavigation(callback: () -> Unit = {}) {
        if(navHostFragment?.parentFragment is HomeFragment) {
            (navHostFragment?.parentFragment as HomeFragment?)?.showNavigation(callback)
        }
    }
}