package com.di7ak.openspaces.ui.features.comments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.di7ak.openspaces.R
import com.di7ak.openspaces.data.ATTACH_TYPE_INTERNAL_VIDEO
import com.di7ak.openspaces.data.entities.Attach
import com.di7ak.openspaces.data.entities.CommentItemEntity
import com.di7ak.openspaces.databinding.CommentsFragmentBinding
import com.di7ak.openspaces.ui.base.BaseFragment
import com.di7ak.openspaces.utils.Resource
import com.di7ak.openspaces.utils.autoCleared
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CommentsFragment : BaseFragment(), CommentsAdapter.CommentsItemListener {
    private var binding: CommentsFragmentBinding by autoCleared()
    private val viewModel: CommentViewModel by viewModels()
    private lateinit var adapter: CommentsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = CommentsFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupObservers()

        val postId = requireArguments().getInt("postId")
        val postUrl = requireArguments().getString("postUrl")!!

        viewModel.fetch(postId, postUrl)
    }

    private fun setupRecyclerView() {
        adapter = CommentsAdapter(this)
        (binding.items.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false

        val dividerItemDecoration = DividerItemDecoration(requireContext(), RecyclerView.VERTICAL)
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.comments_divider)!!)
        binding.items.addItemDecoration(dividerItemDecoration)

        binding.items.layoutManager = LinearLayoutManager(requireContext())
        binding.items.adapter = adapter
    }

    private fun setProgress(progress: Boolean) {
        binding.progress.isGone = !progress
    }

    private fun setupObservers() {
        viewModel.comments.observe(viewLifecycleOwner, {
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
        viewModel.updatedComment.observe(viewLifecycleOwner, {
            adapter.updateItem(it)
        })
    }

    override fun onClickedItem(view: View, item: CommentItemEntity) {

    }

    override fun onClickedDislike(view: View, item: CommentItemEntity) {
        viewModel.like(item, false)
    }

    override fun onClickedLike(view: View, item: CommentItemEntity) {
        viewModel.like(item, true)
    }

    override fun onClickedAttach(view: View, item: Attach) {
        if(item.type == ATTACH_TYPE_INTERNAL_VIDEO) {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(Uri.parse(item.url), "video/mp4")
            startActivity(intent)
        }
    }
}
