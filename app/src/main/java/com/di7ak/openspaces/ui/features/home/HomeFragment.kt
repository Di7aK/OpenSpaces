package com.di7ak.openspaces.ui.features.home

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnticipateInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.doOnPreDraw
import androidx.core.view.isGone
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.di7ak.openspaces.R
import com.di7ak.openspaces.data.CODE_SUCCESS
import com.di7ak.openspaces.data.Session
import com.di7ak.openspaces.databinding.HomeFragmentBinding
import com.di7ak.openspaces.ui.base.BaseFragment
import com.di7ak.openspaces.ui.features.auth.AuthFragment
import com.di7ak.openspaces.utils.Resource
import com.di7ak.openspaces.utils.autoCleared
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : BaseFragment() {
    companion object {
        const val EXTRA_USER_ID = "userId"
    }

    private var binding: HomeFragmentBinding by autoCleared()
    private val viewModel: HomeViewModel by viewModels()
    private lateinit var subNavController: NavController

    @Inject
    lateinit var session: Session

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
            postponeEnterTransition()
            view.doOnPreDraw {
                startPostponedEnterTransition()
            }
        }

        subNavController = binding.fragment.findNavController()

        if (session.current == null) {
            findNavController().navigate(R.id.action_homeFragment_to_authFragment)
        } else {
            setupObservers()
            viewModel.fetchTopCount()
        }

        val appBarConfiguration =
            AppBarConfiguration(setOf(
                R.id.navigation_lenta,
                R.id.navigation_journal,
                R.id.navigation_mail
            ))

        setupActionBarWithNavController(requireActivity() as AppCompatActivity, subNavController, appBarConfiguration)
        binding.navigation.setupWithNavController(subNavController)
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

    private fun setupObservers() {
        viewModel.topCount.observe(viewLifecycleOwner, {
            Log.d("lol", "update home")
            when (it.status) {
                Resource.Status.SUCCESS -> {
                    binding.progress.isGone = true
                    when (it.data?.code) {
                        CODE_SUCCESS -> {
                            setBadges(it.data.journalCnt, it.data.mailCnt)
                            //ThemeColors.setNewThemeColor(requireActivity(), it.data.color)
                        }
                        else -> {
                            val args =
                                bundleOf(AuthFragment.EXTRA_USERNAME to session.current?.name)
                            findNavController().navigate(
                                R.id.action_homeFragment_to_authFragment,
                                args
                            )
                        }
                    }
                }

                Resource.Status.ERROR -> {
                    binding.progress.isGone = true
                    Toast.makeText(activity, it.message, Toast.LENGTH_SHORT).show()
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
            badgeTextColor = ContextCompat.getColor(requireContext(), R.color.colorBadgeText)
            backgroundColor = ContextCompat.getColor(requireContext(), R.color.colorBadge)
            number = journal
        } else binding.navigation.removeBadge(R.id.navigation_journal)

        if (mail != 0) binding.navigation.getOrCreateBadge(R.id.navigation_mail).apply {
            badgeTextColor = ContextCompat.getColor(requireContext(), R.color.colorBadgeText)
            backgroundColor = ContextCompat.getColor(requireContext(), R.color.colorBadge)
            number = mail
        } else binding.navigation.removeBadge(R.id.navigation_mail)
    }
}
