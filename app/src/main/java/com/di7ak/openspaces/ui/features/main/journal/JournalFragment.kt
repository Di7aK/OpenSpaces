package com.di7ak.openspaces.ui.features.main.journal

import android.animation.AnimatorInflater
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.SimpleItemAnimator
import com.di7ak.openspaces.R
import com.di7ak.openspaces.data.JOURNAL_FILTER_ALL
import com.di7ak.openspaces.data.JOURNAL_FILTER_NEW
import com.di7ak.openspaces.data.entities.JournalItemEntity
import com.di7ak.openspaces.databinding.FragmentJournalBinding
import com.di7ak.openspaces.ui.base.BaseFragment
import com.di7ak.openspaces.ui.features.main.comments.CommentsFragment
import com.di7ak.openspaces.ui.utils.MenuDialog
import com.di7ak.openspaces.ui.utils.ProgressAdapter
import com.di7ak.openspaces.utils.*
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class JournalFragment : BaseFragment(), JournalAdapter.JournalItemListener,
    MenuDialog.MenuDialogListener, TabLayout.OnTabSelectedListener {
    private var binding: FragmentJournalBinding by autoCleared()
    private val viewModel: JournalViewModel by viewModels()
    private lateinit var adapter: JournalAdapter
    private val progressAdapter: ProgressAdapter = ProgressAdapter(::retry)

    @Inject
    lateinit var imageGetter: HtmlImageGetter
    private lateinit var filterDialog: MenuDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //setHasOptionsMenu(true)
        setHasNavigationMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        //inflater.inflate(R.menu.journal, menu)
    }

    /*override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.filter) {
            filterDialog.show()
        }
        return super.onOptionsItemSelected(item)
    }*/

    override fun onMenuItemClick(itemId: Int) {
        viewModel.filter = when (itemId) {
            R.id.allRecords -> JOURNAL_FILTER_ALL
            else -> JOURNAL_FILTER_NEW
        }
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

        filterDialog = MenuDialog(requireContext(), R.menu.journal_filter, this)
        setupTabs()
        setupRecyclerView()
        setupObservers()

        viewModel.fetch()
    }

    override fun onBackPressed() {
        animateViewsOut(false) {
            super.onBackPressed()
        }
    }

    private fun setupTabs() {
        val tabNew = binding.tabs.newTab().setText(R.string.new_records).setTag(R.id.newRecords)
        val tabAll = binding.tabs.newTab().setText(R.string.all_records).setTag(R.id.allRecords)
        binding.tabs.addTab(tabNew)
        binding.tabs.addTab(tabAll)

        when (viewModel.filter) {
            JOURNAL_FILTER_ALL -> tabAll.select()
            JOURNAL_FILTER_NEW -> tabNew.select()
        }
        binding.tabs.addOnTabSelectedListener(this)
    }

    private fun retry() {

    }

    private fun animateViewsOut(hideNavigation: Boolean, callback: () -> Unit) {
        AnimatorInflater.loadAnimator(activity, R.animator.main_list_animator).apply {
            setTarget(binding.items)
            withEndAction { callback() }
            start()
        }
        if (hideNavigation) hideNavigation()
    }

    private fun setupRecyclerView() {
        adapter = JournalAdapter(imageGetter, lifecycleScope, this)
        (binding.items.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false

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
        animateViewsOut(true) {
            val args = bundleOf(CommentsFragment.EXTRA_URL to item.link)
            findNavController().navigate(R.id.action_journalFragment_to_commentsFragment, args)
        }
    }

    override fun onTabSelected(tab: TabLayout.Tab?) {
        viewModel.filter = when (tab?.tag) {
            R.id.allRecords -> JOURNAL_FILTER_ALL
            else -> JOURNAL_FILTER_NEW
        }
    }

    override fun onTabUnselected(tab: TabLayout.Tab?) {

    }

    override fun onTabReselected(tab: TabLayout.Tab?) {

    }
}