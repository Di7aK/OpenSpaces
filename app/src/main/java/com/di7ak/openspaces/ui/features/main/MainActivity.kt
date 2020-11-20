package com.di7ak.openspaces.ui.features.main

import android.content.Intent
import android.os.Bundle
import android.view.animation.AnticipateInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.di7ak.openspaces.R
import com.di7ak.openspaces.data.CODE_SUCCESS
import com.di7ak.openspaces.data.Session
import com.di7ak.openspaces.databinding.ActivityMainBinding
import com.di7ak.openspaces.ui.base.BaseActivity
import com.di7ak.openspaces.ui.features.auth.AuthActivity
import com.di7ak.openspaces.ui.features.auth.login.LoginFragment
import com.di7ak.openspaces.utils.Resource
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : BaseActivity() {
    companion object {
        private const val REQUEST_CODE_AUTH = 1
    }
    private lateinit var navController: NavController
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()
    @Inject
    lateinit var session: Session

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)



        if (session.current == null) {
            openAuth()
        } else {
            setupObservers()
            viewModel.fetchTopCount()
        }
    }

    private fun initNavHost() {
        val navHostFragment: NavHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment

        navController = navHostFragment.navController
        navController.setGraph(R.navigation.main)

        val appBarConfiguration =
            AppBarConfiguration(setOf(
                R.id.navigation_lenta,
                R.id.navigation_journal,
                R.id.navigation_mail
            ))

        setupActionBarWithNavController(
            navController,
            appBarConfiguration
        )
        binding.navigation.setupWithNavController(navController)
    }

    private fun openAuth(username: String? = null) {
        startActivityForResult(Intent(this, AuthActivity::class.java).apply {
            putExtra(LoginFragment.EXTRA_USERNAME, username)
        }, REQUEST_CODE_AUTH)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == REQUEST_CODE_AUTH) {
            if(resultCode == RESULT_OK) {
                setupObservers()
                viewModel.fetchTopCount()
            } else {
                finish()
            }
        }
    }

    private fun setupObservers() {
        viewModel.topCount.observe(this, {

            when (it.status) {
                Resource.Status.SUCCESS -> {
                    binding.progress.isGone = true
                    when (it.data?.code) {
                        CODE_SUCCESS -> {
                            initNavHost()
                            setBadges(it.data.journalCnt, it.data.mailCnt)
                            //ThemeColors.setNewThemeColor(requireActivity(), it.data.color)
                        }
                        else -> {
                            openAuth(session.current?.name)
                        }
                    }
                }

                Resource.Status.ERROR -> {
                    binding.progress.isGone = true
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                }

                Resource.Status.LOADING -> {
                    binding.progress.isGone = false
                }
                else -> {
                }
            }
        })
    }

    private fun setBadges(journal: Int, mail: Int) {
        if (journal != 0) binding.navigation.getOrCreateBadge(R.id.navigation_journal).apply {
            badgeTextColor = ContextCompat.getColor(this@MainActivity, R.color.colorBadgeText)
            backgroundColor = ContextCompat.getColor(this@MainActivity, R.color.colorBadge)
            number = journal
        } else binding.navigation.removeBadge(R.id.navigation_journal)

        if (mail != 0) binding.navigation.getOrCreateBadge(R.id.navigation_mail).apply {
            badgeTextColor = ContextCompat.getColor(this@MainActivity, R.color.colorBadgeText)
            backgroundColor = ContextCompat.getColor(this@MainActivity, R.color.colorBadge)
            number = mail
        } else binding.navigation.removeBadge(R.id.navigation_mail)
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