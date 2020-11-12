package com.di7ak.openspaces.ui.features.auth

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.di7ak.openspaces.data.*
import com.di7ak.openspaces.databinding.AuthFragmentBinding
import com.di7ak.openspaces.ui.base.BaseFragment
import com.di7ak.openspaces.utils.Resource
import com.di7ak.openspaces.utils.autoCleared
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthFragment : BaseFragment() {
    private var binding: AuthFragmentBinding by autoCleared()
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = AuthFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObservers()

        setupListeners()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            binding.detailContainer.transitionName = "add_account"
        }
    }

    private fun setupListeners() {
        binding.btnLogin.setOnClickListener {
            val login = binding.loginInput.text.toString()
            val password = binding.passwordInput.text.toString()

            binding.loginInputLayout.error = null
            viewModel.login(login, password)
        }
    }

    private fun setProgress(progress: Boolean) {
        binding.btnLogin.isEnabled = !progress
        binding.btnSingUp.isEnabled = !progress
        binding.loginInput.isEnabled = !progress
        binding.passwordInput.isEnabled = !progress
    }

    private fun setupObservers() {
        viewModel.auth.observe(viewLifecycleOwner, {
            when (it.status) {
                Resource.Status.SUCCESS -> {
                    setProgress(false)
                    when(it.data?.code) {
                        CODE_SUCCESS -> {
                            findNavController().popBackStack()
                        }
                        CODE_NEED_CAPTCHA, CODE_WRONG_CAPTCHA -> {
                            showCaptcha(it.data.captchaUrl)
                        }
                        CODE_WRONG_LOGIN_OR_PASSWORD, CODE_USER_NOT_FOUND -> {
                            binding.loginInputLayout.error = "Wrong login or password"
                            binding.loginInput.requestFocus()
                        }
                    }
                }

                Resource.Status.ERROR -> {
                    setProgress(false)
                    Toast.makeText(activity, it.message, Toast.LENGTH_SHORT).show()
                }

                Resource.Status.LOADING -> {
                    setProgress(true)
                }
            }
        })
    }

    override fun onCaptchaEntered(code: String) {
        super.onCaptchaEntered(code)

        val login = binding.loginInput.text.toString()
        val password = binding.passwordInput.text.toString()

        viewModel.login(login, password, code)
    }
}
