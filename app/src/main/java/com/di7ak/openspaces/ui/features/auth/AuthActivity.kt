package com.di7ak.openspaces.ui.features.auth

import android.os.Bundle
import androidx.activity.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.di7ak.openspaces.R
import com.di7ak.openspaces.databinding.ActivityAuthBinding
import com.di7ak.openspaces.ui.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthActivity : BaseActivity() {
    private val viewModel: AuthViewModel by viewModels()
    private lateinit var navController: NavController
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var binding: ActivityAuthBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        setupObservers()
        viewModel.fetchSessions()
    }

    private fun setupNavController(startDestination: Int) {
        navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment

        val graphInflater = navHostFragment.navController.navInflater
        val graph = graphInflater.inflate(R.navigation.auth)
        navController = navHostFragment.navController

        graph.startDestination = startDestination
        navController.graph = graph

        val appBarConfiguration = AppBarConfiguration(navController.graph)
        binding.toolbar.setupWithNavController(navController, appBarConfiguration)
    }

    private fun setupObservers() {
        viewModel.sessions.observe(this) {
            val destination = if (it.isNotEmpty()) R.id.navigationAccounts else R.id.navigationLogin

            setupNavController(destination)
        }
    }
}