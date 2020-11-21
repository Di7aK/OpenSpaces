package com.di7ak.openspaces.ui.features.main.journal

import android.animation.AnimatorInflater
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.di7ak.openspaces.R
import com.di7ak.openspaces.data.entities.JournalItemEntity
import com.di7ak.openspaces.databinding.FragmentJournalBinding
import com.di7ak.openspaces.ui.base.BaseFragment
import com.di7ak.openspaces.ui.utils.ProgressAdapter
import com.di7ak.openspaces.utils.*
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class JournalFragment : BaseFragment(), JournalAdapter.JournalItemListener {
    private var binding: FragmentJournalBinding by autoCleared()
    private val viewModel: JournalViewModel by viewModels()
    private lateinit var adapter: JournalAdapter
    private val progressAdapter: ProgressAdapter = ProgressAdapter(::retry)
    @Inject
    lateinit var imageGetter: HtmlImageGetter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasNavigationMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentJournalBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupObservers()

        viewModel.fetch()
    }

    override fun onBackPressed() {
        animateViewsOut {
            super.onBackPressed()
        }
    }

    private fun retry() {

    }

    private fun animateViewsOut(callback: () -> Unit) {
        AnimatorInflater.loadAnimator(activity, R.animator.main_list_animator).apply {
            setTarget(binding.items)
            withEndAction { callback() }
            start()
        }
    }

    private fun setupRecyclerView() {
        adapter = JournalAdapter(imageGetter, lifecycleScope, this)
        (binding.items.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false

        binding.items.layoutManager = LinearLayoutManager(requireContext())
        binding.items.adapter = ConcatAdapter(adapter, progressAdapter.apply {
            recyclerView = binding.items
            contentAdapter = adapter
        })
    }

    private fun setItems(items: List<JournalItemEntity>) {
        val replace = adapter.itemCount != 0
        adapter.setItems(ArrayList(items))
        if (!replace && adapter.itemCount == items.size) {
            binding.items.startLayoutAnimation()
        }
        progressAdapter.isEmpty = adapter.itemCount == 0
    }

    private fun setupObservers() {
        viewModel.records.observe(viewLifecycleOwner, {
            when (it.status) {
                Resource.Status.SUCCESS -> {
                    progressAdapter.isProgress = false
                    setItems(it.data!!)
                }

                Resource.Status.ERROR -> {
                    progressAdapter.isError = true
                    Toast.makeText(activity, it.message, Toast.LENGTH_SHORT).show()
                }

                Resource.Status.LOADING -> {
                    progressAdapter.isProgress = true
                }
                else -> {
                }
            }
        })
    }

    override fun onClickedItem(view: View, item: JournalItemEntity) {

    }
}