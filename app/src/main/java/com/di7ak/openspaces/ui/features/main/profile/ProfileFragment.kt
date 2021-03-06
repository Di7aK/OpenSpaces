package com.di7ak.openspaces.ui.features.main.profile

import android.animation.AnimatorInflater
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SwitchCompat
import androidx.core.os.bundleOf
import androidx.core.view.isGone
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.di7ak.openspaces.R
import com.di7ak.openspaces.data.entities.ProfileEntity
import com.di7ak.openspaces.databinding.FragmentProfileBinding
import com.di7ak.openspaces.ui.base.BaseFragment
import com.di7ak.openspaces.ui.features.auth.AuthActivity
import com.di7ak.openspaces.ui.features.main.blogs.BlogsFragment
import com.di7ak.openspaces.ui.features.main.comments.CommentsFragment
import com.di7ak.openspaces.ui.utils.ConfirmDialog
import com.di7ak.openspaces.utils.*
import com.google.android.material.navigation.NavigationView
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment : BaseFragment(), NavigationView.OnNavigationItemSelectedListener {
    companion object {
        private const val REQUEST_CHANGE_ACCOUNT = 1
        private const val REQUEST_LOGOUT = 2

        const val EXTRA_USER_ID = "user_id"
    }
    private var binding: FragmentProfileBinding by autoCleared()
    private val viewModel: ProfileViewModel by viewModels()
    private lateinit var switch: SwitchCompat
    private var isOwner: Boolean = false
    private var userId: Int = 0
    @Inject
    lateinit var imageGetter: HtmlImageGetter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val extraUserId = arguments?.getInt(EXTRA_USER_ID)
        (extraUserId ?: viewModel.session.current?.userId)?.let { userId ->
            this.userId = userId
            viewModel.fetchProfile(userId.toString())

            isOwner = viewModel.session.current?.userId == userId
        }

        setHasNavigationMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupMenu()
        setupObservers()
    }

    private fun setupMenu() {
        if(isOwner) {
            val itemSwitchTheme = binding.menu.menu.findItem(R.id.navigation_switch_theme)
            switch = itemSwitchTheme.actionView.findViewById(R.id.switchView)
            switch.isChecked = requireContext().isNightMode()
            switch.setOnCheckedChangeListener { _, _ ->
                changeTheme()
            }
        }
        binding.menu.setNavigationItemSelectedListener(this)

        binding.menu.menu.findItem(R.id.navigation_logout).isVisible = isOwner
        binding.menu.menu.findItem(R.id.navigation_change_account).isVisible = isOwner
        binding.menu.menu.findItem(R.id.navigation_switch_theme).isVisible = isOwner

        binding.menuCard.startLayoutAnimation()
    }

    private fun bindProfile(profile: ProfileEntity) {
        binding.username.text = profile.name
        binding.fullName.text = profile.fullName
        profile.status.fromHtml(lifecycleScope, imageGetter, {
            binding.status.text = it
        }, binding.status.createDrawableCallback())

        binding.status.isGone = profile.status.isNullOrBlank()

        Glide.with(binding.image)
            .load(profile.avatarPreview)
            .circleCrop()
            .into(binding.image)
        binding.profileCard.startLayoutAnimation()
    }

    private fun setProgress(isProgress: Boolean) {
        binding.profileCard.isInvisible = isProgress
        binding.progress.isVisible = isProgress
    }

    private fun setupObservers() {
        viewModel.profile.observe(viewLifecycleOwner) {
            when (it.status) {
                Resource.Status.SUCCESS -> {
                    setProgress(false)
                    bindProfile(it.data!!)
                }

                Resource.Status.ERROR -> {
                    setProgress(false)
                }

                Resource.Status.LOADING -> {
                    setProgress(true)
                }
                else -> { }
            }
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.navigation_guest_book -> {
                hideNavigation {
                    animateViewsOut {
                        val args =
                            bundleOf(CommentsFragment.EXTRA_GUEST_BOOK_USER to userId)
                        findNavController().navigate(
                            R.id.action_profileFragment_to_commentsFragment,
                            args
                        )
                    }
                }
            }

            R.id.navigation_blogs -> {
                    animateViewsOut {
                        val args =
                            bundleOf(BlogsFragment.ARG_USER to viewModel.profile.value?.data?.name)
                        findNavController().navigate(
                            R.id.action_navigation_profile_to_navigation_blogs,
                            args
                        )
                    }
            }

            R.id.navigation_change_account -> changeAccount()
            R.id.navigation_logout -> logout()

            R.id.navigation_switch_theme -> {
                switch.isChecked = !switch.isChecked
                changeTheme()
            }
        }
        return true
    }

    private fun changeAccount() {
        animateViewsOut {
            startActivityForResult(
                Intent(requireContext(), AuthActivity::class.java),
                REQUEST_CHANGE_ACCOUNT
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == REQUEST_CHANGE_ACCOUNT) {
            requireActivity().recreate()
        }

        if(requestCode == REQUEST_LOGOUT) {
            if(resultCode == Activity.RESULT_OK) {
                requireActivity().recreate()
            } else {
                requireActivity().finish()
            }
        }
    }

    private fun changeTheme() {
        val isNight = requireContext().isNightMode()
        ThemeColors.enableNightTheme(requireActivity(), !isNight)
    }

    private fun logout() {
        ConfirmDialog(requireContext(),
            icon = R.drawable.logout_variant,
            title = R.string.remove_account,
            subtitle = R.string.logout_confirm,
            listener = object : ConfirmDialog.ConfirmDialogListener {
                override fun onAccept() {
                    viewModel.logout()
                    animateViewsOut {
                        startActivityForResult(
                            Intent(requireContext(), AuthActivity::class.java),
                            REQUEST_LOGOUT
                        )
                    }
                }
            }
        ).show()
    }

    override fun onBackPressed() {
        animateViewsOut(!isOwner) {
            super.onBackPressed()
        }
    }

    private fun animateViewsOut(hideNavigation: Boolean = false, callback: () -> Unit) {
        binding.menu.isEnabled = false
        AnimatorInflater.loadAnimator(activity, R.animator.main_list_animator).apply {
            setTarget(binding.menuCard)
            start()
        }
        AnimatorInflater.loadAnimator(activity, R.animator.main_list_animator).apply {
            setTarget(binding.profileCard)
            withEndAction  { callback() }
            start()
        }
        if(hideNavigation) hideNavigation()
    }
}
