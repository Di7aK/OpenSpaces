package com.di7ak.openspaces.ui.features.auth.login

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.di7ak.openspaces.R
import com.di7ak.openspaces.data.*
import com.di7ak.openspaces.databinding.FragmentLoginBinding
import com.di7ak.openspaces.ui.base.BaseFragment
import com.di7ak.openspaces.ui.features.scanner.ScannerActivity
import com.di7ak.openspaces.utils.Resource
import com.di7ak.openspaces.utils.autoCleared
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : BaseFragment() {
    companion object {
        const val EXTRA_USERNAME = "username"
        const val REQUEST_QR_CODE = 3
        private const val REQUEST_PERMISSIONS = 4
    }
    private var binding: FragmentLoginBinding by autoCleared()
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObservers()
        setupListeners()

        arguments?.getString(EXTRA_USERNAME)?.let { username ->
            binding.loginInput.setText(username)
            Toast.makeText(requireContext(), getString(R.string.auth_required), Toast.LENGTH_SHORT).show()
        }

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
        binding.btnSingInByQrCode.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                checkPermissions()
            } else startScan()
        }
    }

    private fun startScan() {
        startActivityForResult(Intent(requireContext(), ScannerActivity::class.java), REQUEST_QR_CODE)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun checkPermissions() {
        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) -> {
                startScan()
            }
            else -> {
                requestPermissions(arrayOf(Manifest.permission.CAMERA),
                    REQUEST_PERMISSIONS
                )
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_PERMISSIONS -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    startScan()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == REQUEST_QR_CODE && resultCode == Activity.RESULT_OK) {
            val sid = data!!.getStringExtra(ScannerActivity.RESULT_TEXT)!!
            viewModel.loginBySid(sid)
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
                    when (it.data?.code) {
                        CODE_SUCCESS -> {
                            viewModel.currentSession = it.data.attributes
                            openAccount()
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
                else -> {
                }
            }
        })
    }

    private fun openAccount() {
        setResult(Activity.RESULT_OK)
    }

    override fun onCaptchaEntered(code: String) {
        super.onCaptchaEntered(code)

        val login = binding.loginInput.text.toString()
        val password = binding.passwordInput.text.toString()

        viewModel.login(login, password, code)
    }
}
