package com.di7ak.openspaces.ui.features.player


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.di7ak.openspaces.R
import com.di7ak.openspaces.data.entities.Attach
import com.di7ak.openspaces.data.entities.LentaItemEntity
import com.di7ak.openspaces.databinding.PlayerFragmentBinding
import com.di7ak.openspaces.ui.features.comments.CommentsFragment
import com.di7ak.openspaces.ui.features.home.HomeFragment
import com.di7ak.openspaces.ui.features.lenta.LentaFragment
import kotlin.math.abs

private const val ITEM = "item"
private const val ATTACH = "attach"

class PlayerFragment: Fragment() {
    private lateinit var binding: PlayerFragmentBinding

    companion object {
        fun newInstance(item: LentaItemEntity, attach: Attach): PlayerFragment = PlayerFragment().also { f ->
            f.arguments = Bundle().also { b ->
                b.putParcelable(ITEM, item)
                b.putParcelable(ATTACH, attach)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = PlayerFragmentBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (arguments?.containsKey(ATTACH) == true) {
            val attach = requireArguments().getParcelable<Attach>(ATTACH)
            //binding.videoView.setSource(attach!!.url)
            //binding.videoView.setPlayWhenReady(true)
        }

        if (arguments?.containsKey(ITEM) == true) {
            val item = requireArguments().getParcelable<LentaItemEntity>(ITEM)

            val commentsFragment = CommentsFragment()
            commentsFragment.arguments = bundleOf(CommentsFragment.EXTRA_POST to item)
                childFragmentManager.beginTransaction()
                    .replace(R.id.commentsContainer, commentsFragment)
                    .commit()
        }

        val parent = parentFragment
        val parent2 = parent?.parentFragment
        val parent3 = parent2?.parentFragment
        val parent4 = parent3?.parentFragment
        binding.videoMotionLayout.setTransitionListener(object : MotionLayout.TransitionListener {
            override fun onTransitionChange(motionLayout: MotionLayout?, startId: Int, endId: Int, progress: Float) {
                (parent3 as HomeFragment).progress = abs(progress)
            }

            override fun onTransitionCompleted(motionLayout: MotionLayout?, currentId: Int) {

            }

            override fun onTransitionStarted(p0: MotionLayout?, p1: Int, p2: Int) {
            }

            override fun onTransitionTrigger(p0: MotionLayout?, p1: Int, p2: Boolean, p3: Float) {
            }
        })
        binding.videoMotionLayout.transitionToEnd()
    }
}