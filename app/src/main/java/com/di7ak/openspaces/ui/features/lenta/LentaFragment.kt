package com.di7ak.openspaces.ui.features.lenta

import android.animation.AnimatorInflater
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.transition.TransitionInflater
import android.view.*
import android.view.animation.Animation
import android.view.animation.OvershootInterpolator
import android.widget.ImageView
import androidx.core.animation.addListener
import androidx.core.os.bundleOf
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.SimpleItemAnimator
import com.bumptech.glide.Glide
import com.di7ak.openspaces.R
import com.di7ak.openspaces.data.*
import com.di7ak.openspaces.data.entities.Attach
import com.di7ak.openspaces.data.entities.LentaItemEntity
import com.di7ak.openspaces.databinding.LentaFragmentBinding
import com.di7ak.openspaces.ui.base.BaseSubFragment
import com.di7ak.openspaces.ui.features.comments.CommentsFragment
import com.di7ak.openspaces.ui.features.player.PlayerFragment
import com.di7ak.openspaces.ui.utils.ProgressAdapter
import com.di7ak.openspaces.utils.*
import com.stfalcon.imageviewer.StfalconImageViewer
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.lenta_fragment.view.*
import javax.inject.Inject

@AndroidEntryPoint
class LentaFragment : BaseSubFragment(), LentaAdapter.LentaItemListener {
    private var binding: LentaFragmentBinding by autoCleared()
    private val viewModel: LentaViewModel by viewModels()
    @Inject
    lateinit var imageGetter: HtmlImageGetter

    @Inject
    lateinit var attachmentParser: AttachmentParser

    @Inject
    lateinit var session: Session

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

        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }

        viewModel.fetch()
    }

    private fun retry() {
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)
        setHasNavigationMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.guestbook, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.guestbook) {
            val args = bundleOf(CommentsFragment.EXTRA_GUEST_BOOK_USER to session.current?.userId)
            findNavController().navigate(R.id.action_lentaFragment_to_commentsFragment, args)
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setProgress(progress: Boolean) {
        progressAdapter.isProgress = progress
    }

    private fun setupObservers() {
        viewModel.events.observe(viewLifecycleOwner, {
            when (it.status) {
                Resource.Status.SUCCESS -> {
                    setProgress(false)
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
        openPost(view, item)
    }

    override fun onClickedDislike(view: View, item: LentaItemEntity) {
        viewModel.like(item, false)
    }

    override fun onClickedLike(view: View, item: LentaItemEntity) {
        viewModel.like(item, true)
    }

    override fun onClickedAttach(view: View, attach: Attach, item: LentaItemEntity) {
        when (attach.type) {
            ATTACH_TYPE_INTERNAL_VIDEO -> {
                childFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, PlayerFragment.newInstance(item, attach))
                    .commit()
                /*val intent = Intent(Intent.ACTION_VIEW)
                intent.setDataAndType(Uri.parse(attach.url), "video/mp4")
                startActivity(intent)*/
            }
            ATTACH_TYPE_EXTERNAL_VIDEO -> {
                val url = if(attach.sourceType == SOURCE_TYPE_YOUTUBE) {
                    "https://www.youtube.com/watch?v=${attach.videoId}"
                } else attach.externalUrl
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(url)
                startActivity(intent)
            }
            ATTACH_TYPE_INTERNAL_IMAGE -> {
                val items = item.attachments.filter { it.type == ATTACH_TYPE_INTERNAL_IMAGE }
                StfalconImageViewer.Builder(context, items) { target, image ->
                    Glide.with(target).load(image.url).placeholder((view as ImageView).drawable).into(target)
                }.withTransitionFrom(view as ImageView)
                    .allowZooming(true)
                    .show()
            }
        }
    }

    private fun openPost(view: View, item: LentaItemEntity) {
        val args = bundleOf(CommentsFragment.EXTRA_POST to item)
        supportsLollipop {
            view.transitionName = "post"
        }
        val extras = FragmentNavigatorExtras(view to "post${item.id}")

        hideNavigation {  }
        findNavController().navigate(R.id.action_lentaFragment_to_commentsFragment, args, null, extras)

        /*AnimatorInflater.loadAnimator(activity, R.animator.main_list_animator).apply {
            setTarget(binding.items)
            withEndAction {
                //binding.items.visibility = View.INVISIBLE
                view.visibility = View.VISIBLE

                hideNavigation {
                    findNavController().navigate(R.id.action_lentaFragment_to_commentsFragment, args, null, extras)
                }
                //view.animate().setDuration(400).y(0f).setInterpolator(OvershootInterpolator()).start()
            }
            start()
        }*/
    }
}
