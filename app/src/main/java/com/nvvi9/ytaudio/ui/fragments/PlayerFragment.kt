package com.nvvi9.ytaudio.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.nvvi9.ytaudio.databinding.FragmentPlayerBinding
import com.nvvi9.ytaudio.di.Injectable
import com.nvvi9.ytaudio.ui.MainActivity
import com.nvvi9.ytaudio.ui.viewmodels.MainActivityViewModel
import com.nvvi9.ytaudio.ui.viewmodels.PlayerViewModel
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject
import kotlin.math.abs


class PlayerFragment : Fragment(), MotionLayout.TransitionListener, Injectable {

    @Inject
    lateinit var mainActivityViewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var playerViewModelFactory: ViewModelProvider.Factory

    private lateinit var binding: FragmentPlayerBinding

    private val mainActivityViewModel by viewModels<MainActivityViewModel> {
        mainActivityViewModelFactory
    }

    private val playerViewModel by viewModels<PlayerViewModel> {
        playerViewModelFactory
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = FragmentPlayerBinding.inflate(inflater).apply {
        playPauseButton.setOnClickListener {
            playerViewModel.currentAudioInfo.value?.id?.let {
                mainActivityViewModel.playAudio(it)
            }
        }

        viewModel = playerViewModel

        motionLayout.setTransitionListener(this@PlayerFragment)
    }.also { binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        playerViewModel.run {
            currentAudioInfo.observe(viewLifecycleOwner) {
                binding.nowPlaying = it
                Log.i(javaClass.simpleName, it?.toString() ?: "empty")
            }

            currentButtonRes.observe(viewLifecycleOwner) {
                binding.buttonRes = it
            }

            currentPosition.observe(viewLifecycleOwner) {
                binding.position = it
            }
        }
    }

    override fun onTransitionChange(p0: MotionLayout?, p1: Int, p2: Int, p3: Float) {
        (activity as MainActivity).main_motion_layout.progress = abs(p3)
    }

    override fun onTransitionStarted(p0: MotionLayout?, p1: Int, p2: Int) {
//        if (p1 == R.id.end) {
//            (activity as MainActivity).binding.mainMotionLayout.transitionToStart()
//        } else {
//            (activity as MainActivity).binding.mainMotionLayout.transitionToEnd()
//        }
    }

    override fun onTransitionCompleted(p0: MotionLayout?, p1: Int) {}

    override fun onTransitionTrigger(p0: MotionLayout?, p1: Int, p2: Boolean, p3: Float) {}

    companion object {
        fun newInstance() = PlayerFragment()
    }
}