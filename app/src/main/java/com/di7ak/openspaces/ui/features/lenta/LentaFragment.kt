package com.di7ak.openspaces.ui.features.lenta

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isGone
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.di7ak.openspaces.databinding.LentaFragmentBinding
import com.di7ak.openspaces.ui.base.BaseFragment
import com.di7ak.openspaces.utils.Resource
import com.di7ak.openspaces.utils.autoCleared
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LentaFragment : BaseFragment(), LentaAdapter.LentaItemListener {
    private var binding: LentaFragmentBinding by autoCleared()
    private val viewModel: LentaViewModel by viewModels()
    private lateinit var adapter: LentaAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = LentaFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupObservers()

        val userId = requireArguments().getInt("userId")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            binding.detailContainer.transitionName = userId.toString()
        }

        viewModel.fetch()
    }

    private fun setupRecyclerView() {
        adapter = LentaAdapter(this)
        binding.items.layoutManager = LinearLayoutManager(requireContext())
        binding.items.adapter = adapter
    }

    private fun setProgress(progress: Boolean) {
        binding.progress.isGone = !progress
    }

    private fun setupObservers() {
        viewModel.events.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Resource.Status.SUCCESS -> {
                    setProgress(false)
                    adapter.setItems(ArrayList(it.data!!))
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

    override fun onClickedItem(view: View, item: LentaModel) {

    }
}
