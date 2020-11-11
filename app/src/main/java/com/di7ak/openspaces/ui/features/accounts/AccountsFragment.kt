package com.di7ak.openspaces.ui.features.accounts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.di7ak.openspaces.R
import com.di7ak.openspaces.data.entities.AuthAttributes
import com.di7ak.openspaces.databinding.AccountsFragmentBinding
import com.di7ak.openspaces.ui.base.BaseFragment
import com.di7ak.openspaces.ui.utils.ConfirmDialog
import com.di7ak.openspaces.utils.autoCleared
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AccountsFragment : BaseFragment(), AccountsAdapter.AccountsItemListener {
    private var binding: AccountsFragmentBinding by autoCleared()
    private val viewModel: AccountsViewModel by viewModels()
    private lateinit var adapter: AccountsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = AccountsFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupObservers()
        setupListeners()

        postponeEnterTransition ()
        view.doOnPreDraw {startPostponedEnterTransition ()}

        viewModel.fetchSessions()
    }

    private fun setupListeners() {
        ViewCompat.setTransitionName(binding.btnAddAccount, "add_account")
        binding.btnAddAccount.setOnClickListener {
            val extras = FragmentNavigatorExtras(binding.btnAddAccount to "add_account")
            findNavController().navigate(R.id.action_accountsFragment_to_authFragment, null, null, extras)
        }
    }

    private fun setupRecyclerView() {
        adapter = AccountsAdapter(this)
        binding.accountsList.layoutManager = LinearLayoutManager(requireContext())
        binding.accountsList.adapter = adapter
    }

    private fun setupObservers() {
        viewModel.sessions.observe(viewLifecycleOwner, {
            adapter.setItems(ArrayList(it))
        })
    }

    override fun onClickedSession(view: View, session: AuthAttributes) {
        viewModel.currentSession = session

        val extras = FragmentNavigatorExtras(view to session.userId.toString())
        findNavController().navigate(R.id.action_accountsFragment_to_lentaFragment, bundleOf("userId" to session.userId), null, extras)
    }

    override fun onClickedDelete(session: AuthAttributes) {
        ConfirmDialog(requireContext(),
            icon = R.drawable.account_remove_outline,
            title = R.string.remove_account,
            subtitle = R.string.remove_account_description,
            listener = object : ConfirmDialog.ConfirmDialogListener {
                override fun onAccept() {
                    viewModel.deleteSession(session)
                }
            }
        ).show()
    }
}
