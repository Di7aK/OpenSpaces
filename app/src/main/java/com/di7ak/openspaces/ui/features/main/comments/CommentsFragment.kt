package com.di7ak.openspaces.ui.features.main.comments

import android.animation.AnimatorInflater
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnticipateInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.isGone
import androidx.core.view.isInvisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.*
import com.di7ak.openspaces.R
import com.di7ak.openspaces.data.ATTACH_TYPE_INTERNAL_VIDEO
import com.di7ak.openspaces.data.entities.Attach
import com.di7ak.openspaces.data.entities.CommentItemEntity
import com.di7ak.openspaces.databinding.FragmentCommentsBinding
import com.di7ak.openspaces.ui.base.BaseFragment
import com.di7ak.openspaces.ui.features.main.profile.ProfileFragment
import com.di7ak.openspaces.ui.utils.ProgressAdapter
import com.di7ak.openspaces.utils.*
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CommentsFragment : BaseFragment(), CommentsAdapter.CommentsItemListener {
    companion object {
        const val EXTRA_POST = "post"
        const val EXTRA_URL = "url"
        const val EXTRA_GUEST_BOOK_USER = "guest_book_user"
    }

    private var binding: FragmentCommentsBinding by autoCleared()
    private val viewModel: CommentViewModel by viewModels()
    private lateinit var adapter: CommentsAdapter
    private val progressAdapter: ProgressAdapter = ProgressAdapter(::retry)
    @Inject
    lateinit var imageGetter: HtmlImageGetter
    @Inject
    lateinit var attachmentParser: AttachmentParser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasNavigationMenu(false)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCommentsBinding.inflate(inflater, container, false)
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
        viewModel.guestBookUser = requireArguments().getInt(EXTRA_GUEST_BOOK_USER)

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
        val translateTo = binding.commentForm.commentFormContainer.height * 2f
        AnimatorInflater.loadAnimator(activity, R.animator.main_list_animator).apply {
            setTarget(binding.items)
            start()
        }

        binding.commentForm.commentFormContainer.animate()
            .translationY(translateTo)
            .setDuration(350)
            .setInterpolator(AnticipateInterpolator(2f))
            .withEndAction { callback() }
            .start()
    }

    private fun setupRecyclerView() {
        adapter = CommentsAdapter(imageGetter, lifecycleScope, this)
        (binding.items.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false

        binding.items.layoutManager = LinearLayoutManager(requireContext())
        binding.items.adapter = ConcatAdapter(adapter, progressAdapter.apply {
            recyclerView = binding.items
            contentAdapter = adapter
        })
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
            showEditForm(false)
        }
        binding.commentForm.input.addTextChangedListener {
            binding.commentForm.btnSend.isEnabled = !it.isNullOrBlank()
        }
    }

    private fun setItems(items: List<CommentItemEntity>) {
        val replace = adapter.itemCount != 0
        adapter.setItems(ArrayList(items))
        if (!replace && adapter.itemCount == items.size) {
            binding.items.startLayoutAnimation()
            binding.commentForm.commentFormContainer.animate()
                .translationY(0f)
                .setStartDelay(100)
                .setDuration(450)
                .setInterpolator(OvershootInterpolator(2f))
                .start()
        }
        progressAdapter.isEmpty = adapter.itemCount == 0
    }

    private fun setupObservers() {
        viewModel.comments.observe(viewLifecycleOwner, {
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
                else -> {
                }
            }
        })
        viewModel.editComment.observe(viewLifecycleOwner, {
            binding.commentForm.input.setText("")
            adapter.updateItem(it)
            showEditForm(false)
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
        if (!show) viewModel.replyTo = 0
    }

    private fun showEditForm(show: Boolean) {
        binding.commentForm.btnClose.isGone = !show
        binding.commentForm.name.isGone = !show
        binding.commentForm.replyBody.isGone = true
        binding.commentForm.divider.isGone = !show
        if (viewModel.edit != null) binding.commentForm.input.setText("")
        if (!show) viewModel.edit = null
        else {
            binding.commentForm.name.text = getString(R.string.editing_comment)
        }
    }

    override fun onClickedDislike(view: View, item: CommentItemEntity) {
        viewModel.like(item, false)
    }

    override fun onClickedLike(view: View, item: CommentItemEntity) {
        viewModel.like(item, true)
    }

    override fun onClickedMenuItemClick(view: View?, action: Int, item: CommentItemEntity) {
        when (action) {
            R.id.copyText -> {
                val clipboardManager =
                    requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                clipboardManager.setPrimaryClip(
                    ClipData.newPlainText(
                        "open spaces",
                        item.body.fromHtml()
                    )
                )
                Toast.makeText(requireContext(), "Text copied", Toast.LENGTH_SHORT).show()
            }
            R.id.delete -> {
                viewModel.delete(item)
            }
            R.id.edit -> {
                viewModel.edit = item
                showEditForm(true)
                binding.commentForm.input.setText(item.body.fromHtml())
            }
        }
    }

    override fun onClickedUser(view: View?, item: CommentItemEntity) {
        animateViewsOut {
            val args = bundleOf(ProfileFragment.EXTRA_USER_ID to item.author?.id?.toInt())
            findNavController().navigate(R.id.action_commentsFragment_to_profileFragment, args)
        }

    }

    override fun onClickedAttach(view: View, item: Attach) {
        if (item.type == ATTACH_TYPE_INTERNAL_VIDEO) {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(Uri.parse(item.url), "video/mp4")
            startActivity(intent)
        }
    }
}