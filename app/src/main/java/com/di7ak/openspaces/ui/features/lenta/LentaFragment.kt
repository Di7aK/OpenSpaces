package com.di7ak.openspaces.ui.features.lenta

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.SimpleItemAnimator
import com.di7ak.openspaces.R
import com.di7ak.openspaces.data.ATTACH_TYPE_EXTERNAL_VIDEO
import com.di7ak.openspaces.data.ATTACH_TYPE_INTERNAL_VIDEO
import com.di7ak.openspaces.data.SOURCE_TYPE_YOUTUBE
import com.di7ak.openspaces.data.entities.Attach
import com.di7ak.openspaces.data.entities.LentaItemEntity
import com.di7ak.openspaces.databinding.LentaFragmentBinding
import com.di7ak.openspaces.ui.base.BaseFragment
import com.di7ak.openspaces.ui.features.comments.CommentsFragment
import com.di7ak.openspaces.ui.utils.ProgressAdapter
import com.di7ak.openspaces.utils.*
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LentaFragment : BaseFragment(), LentaAdapter.LentaItemListener {
    companion object {
        const val EXTRA_USER_ID = "userId"
    }
    private var binding: LentaFragmentBinding by autoCleared()
    private val viewModel: LentaViewModel by viewModels()
    @Inject
    lateinit var imageGetter: HtmlImageGetter

    @Inject
    lateinit var attachmentParser: AttachmentParser

    private lateinit var adapter: LentaAdapter
    private val progressAdapter: ProgressAdapter = ProgressAdapter(::retry)

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

        val userId = requireArguments().getInt(EXTRA_USER_ID)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            binding.detailContainer.transitionName = userId.toString()
        }

        viewModel.fetch()
    }

    private fun retry() {
        Log.d("lol", "retry: ")
        if(adapter.isEmpty()) viewModel.fetch()
        else viewModel.fetchNext()
    }

    private fun setupRecyclerView() {
        adapter = LentaAdapter(imageGetter, attachmentParser, lifecycleScope, this)
        (binding.items.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        binding.items.adapter = ConcatAdapter(adapter, progressAdapter)

        binding.items.addOnScrollToBottomListener(2) {
            if(viewModel.events.value?.status == Resource.Status.SUCCESS) {
                viewModel.fetchNext()
            }
        }
    }

    private fun setProgress(progress: Boolean) {
        progressAdapter.isProgress = progress
    }

    private fun setupObservers() {
        viewModel.events.observe(viewLifecycleOwner, {
            when (it.status) {
                Resource.Status.SUCCESS -> {
                    setProgress(false)
                    Log.d("lol", "next: ${it.data?.nextLinkUrl}")
                    adapter.setItems(ArrayList(it.data?.items ?: listOf()))
                }

                Resource.Status.ERROR -> {
                    setProgress(false)
                    progressAdapter.isError = true
                }

                Resource.Status.LOADING -> {
                    setProgress(true)
                }
                else -> {}
            }
        })
        viewModel.updatedEvent.observe(viewLifecycleOwner, {
            adapter.updateItem(it)
        })
    }

    override fun onClickedItem(view: View, item: LentaItemEntity) {
        val args = bundleOf(CommentsFragment.EXTRA_POST to item)
        findNavController().navigate(R.id.action_lentaFragment_to_commentsFragment, args)
    }

    override fun onClickedDislike(view: View, item: LentaItemEntity) {
        viewModel.like(item, false)
    }

    override fun onClickedLike(view: View, item: LentaItemEntity) {
        viewModel.like(item, true)
    }

    override fun onClickedAttach(view: View, item: Attach) {
        if(item.type == ATTACH_TYPE_INTERNAL_VIDEO) {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(Uri.parse(item.url), "video/mp4")
            startActivity(intent)
        } else if(item.type == ATTACH_TYPE_EXTERNAL_VIDEO) {
            val url = if(item.sourceType == SOURCE_TYPE_YOUTUBE) {
                "https://www.youtube.com/watch?v=${item.videoId}"
            } else ""
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
        }
    }
}
