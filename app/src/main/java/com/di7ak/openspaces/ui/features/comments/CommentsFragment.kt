package com.di7ak.openspaces.ui.features.comments

import android.content.*
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isGone
import androidx.core.view.isInvisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
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
import com.di7ak.openspaces.utils.*
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CommentsFragment : BaseFragment(), CommentsAdapter.CommentsItemListener {
    companion object {
        const val EXTRA_POST = "post"
        const val EXTRA_URL = "url"
    }
    private var binding: CommentsFragmentBinding by autoCleared()
    private val viewModel: CommentViewModel by viewModels()
    private lateinit var adapter: CommentsAdapter
    @Inject
    lateinit var imageGetter: HtmlImageGetter

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
        setupListeners()
        showReplyForm(false)

        viewModel.post = requireArguments().getParcelable(EXTRA_POST)
        viewModel.url = requireArguments().getString(EXTRA_URL)

        viewModel.fetch()
    }

    private fun setupRecyclerView() {
        adapter = CommentsAdapter(imageGetter, lifecycleScope, this)
        (binding.items.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false

        val dividerItemDecoration = DividerItemDecoration(requireContext(), RecyclerView.VERTICAL)
        dividerItemDecoration.setDrawable(requireContext().getDrawableFromAttr(R.attr.dividerDrawable))
        binding.items.addItemDecoration(dividerItemDecoration)

        binding.items.layoutManager = LinearLayoutManager(requireContext())
        binding.items.adapter = adapter
    }

    private fun setProgress(progress: Boolean) {
        binding.progress.isGone = !progress
    }

    private fun setCommentProgress(progress: Boolean) {
        binding.commentForm.btnSend.isInvisible = progress
        binding.commentForm.progress.isGone = !progress
        binding.commentForm.input.isEnabled = !progress
    }

    private fun setupListeners() {
        binding.commentForm.btnSend.setOnClickListener {
            val comment = binding.commentForm.input.text.toString()
            viewModel.add(comment)
        }

        binding.commentForm.btnClose.setOnClickListener {
            showReplyForm(false)
        }
    }

    private fun setupObservers() {
        viewModel.comments.observe(viewLifecycleOwner, {
            when (it.status) {
                Resource.Status.SUCCESS -> {
                    setProgress(false)
                    it.data?.find { it.id == viewModel.replyTo }?.apply {
                        onClickedReply(null, this)
                    }
                    adapter.setItems(ArrayList(it.data!!))
                }

                Resource.Status.ERROR -> {
                    setProgress(false)
                    Toast.makeText(activity, it.message, Toast.LENGTH_SHORT).show()
                }

                Resource.Status.LOADING -> {
                    setProgress(true)
                }
                else -> {}
            }
        })
        viewModel.updatedComment.observe(viewLifecycleOwner, {
            adapter.updateItem(it)
        })
        viewModel.comment.observe(viewLifecycleOwner, {
            when (it.status) {
                Resource.Status.SUCCESS -> {
                    showReplyForm(false)
                    setCommentProgress(false)
                    binding.commentForm.input.setText("")
                    adapter.addItem(it.data!!)
                }

                Resource.Status.ERROR -> {
                    setCommentProgress(false)
                    Toast.makeText(activity, it.message, Toast.LENGTH_SHORT).show()
                }

                Resource.Status.LOADING -> {
                    setCommentProgress(true)
                }
                else -> {}
            }
        })
        viewModel.editComment.observe(viewLifecycleOwner, {
            binding.commentForm.input.setText("")
            adapter.updateItem(it)
        })
        viewModel.deletedComment.observe(viewLifecycleOwner, {
            adapter.deleteItem(it)
        })
    }

    override fun onClickedItem(view: View, item: CommentItemEntity) {

    }

    override fun onClickedReply(view: View?, item: CommentItemEntity) {
        showReplyForm(true)

        binding.commentForm.name.text = item.author?.name ?: ""
        item.body.fromHtml(lifecycleScope, imageGetter, {
            binding.commentForm.replyBody.text = it
        }, binding.commentForm.replyBody.createDrawableCallback())
        viewModel.replyTo = item.id
    }

    private fun showReplyForm(show: Boolean) {
        binding.commentForm.btnClose.isGone = !show
        binding.commentForm.name.isGone = !show
        binding.commentForm.replyBody.isGone = !show
        binding.commentForm.divider.isGone = !show
        if(!show) viewModel.replyTo = 0
    }

    override fun onClickedDislike(view: View, item: CommentItemEntity) {
        viewModel.like(item, false)
    }

    override fun onClickedLike(view: View, item: CommentItemEntity) {
        viewModel.like(item, true)
    }

    override fun onClickedMenuItemClick(view: View?, action: Int, item: CommentItemEntity) {
        when(action) {
            R.id.copyText -> {
                val clipboardManager = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                clipboardManager.setPrimaryClip(ClipData.newPlainText("open spaces", item.body.fromHtml()))
                Toast.makeText(requireContext(), "Text copied", Toast.LENGTH_SHORT).show()
            }
            R.id.delete -> {
                viewModel.delete(item)
            }
            R.id.edit -> {
                viewModel.editId = item.id
                binding.commentForm.input.setText(item.body.fromHtml())
            }
        }
    }

    override fun onClickedAttach(view: View, item: Attach) {
        if(item.type == ATTACH_TYPE_INTERNAL_VIDEO) {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(Uri.parse(item.url), "video/mp4")
            startActivity(intent)
        }
    }
}
