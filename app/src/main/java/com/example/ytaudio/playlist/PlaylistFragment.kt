package com.example.ytaudio.playlist

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ytaudio.MainActivityViewModel
import com.example.ytaudio.R
import com.example.ytaudio.adapter.ClickListener
import com.example.ytaudio.database.AudioInfo
import com.example.ytaudio.databinding.PlaylistFragmentBinding
import com.example.ytaudio.service.MEDIA_ROOT_ID
import com.example.ytaudio.utils.FactoryUtils


class PlaylistFragment : Fragment() {

    private lateinit var binding: PlaylistFragmentBinding
    private lateinit var playlistViewModel: PlaylistViewModel
    private lateinit var mainActivityViewModel: MainActivityViewModel
    private var actionMode: ActionMode? = null
    private val playlistAdapter = PlaylistAdapter(AdapterAudioInfoListener())


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = PlaylistFragmentBinding.inflate(inflater)
        val application = requireNotNull(this.activity).application
        val audioId = MEDIA_ROOT_ID

        playlistViewModel = ViewModelProvider(
            this,
            FactoryUtils.providePlaylistViewModel(audioId, application)
        ).get(PlaylistViewModel::class.java)

        mainActivityViewModel =
            ViewModelProvider(this, FactoryUtils.provideMainActivityViewModel(application))
                .get(MainActivityViewModel::class.java)

        playlistViewModel.networkFailure.observe(viewLifecycleOwner, Observer {
            binding.networkFailure.visibility = if (it) View.VISIBLE else View.GONE
        })

        binding.viewModel = playlistViewModel

        binding.apply {

            playlistView.adapter = playlistAdapter
            playlistView.layoutManager =
                LinearLayoutManager(application, RecyclerView.VERTICAL, false)
            playlistView.addItemDecoration(
                DividerItemDecoration(
                    activity,
                    DividerItemDecoration.VERTICAL
                )
            )

            lifecycleOwner = this@PlaylistFragment
        }

        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.overflow_playlist_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.delete -> {
                if (!playlistViewModel.databaseAudioInfo.value.isNullOrEmpty()) {
                    startActionMode()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun startActionMode() {
        if (actionMode == null) {
            actionMode = activity?.startActionMode(actionModeCallback)
            playlistAdapter.startActionMode()
            actionMode?.title =
                getString(R.string.selected_items, playlistAdapter.selectedItems.size)
        }
    }


    private val actionModeCallback = object : ActionMode.Callback {

        override fun onCreateActionMode(mode: ActionMode?, menu: Menu?) = mode?.run {
            menuInflater.inflate(R.menu.playlist_toolbar_action_mode, menu)
            true
        } ?: false

        override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
            return when (item?.itemId) {
                R.id.action_select_all -> {
                    playlistAdapter.selectAll()
                    actionMode?.title =
                        getString(R.string.selected_items, playlistAdapter.selectedItems.size)
                    true
                }
                R.id.action_delete -> {
                    mainActivityViewModel.deleteAudioInfo(
                        playlistAdapter.selectedItems
                            .map { it.audioId }
                    )
                    playlistAdapter.stopActionMode()
                    mode?.finish()
                    actionMode = null
                    true
                }
                else -> false
            }
        }

        override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?) = false

        override fun onDestroyActionMode(mode: ActionMode?) {
            playlistAdapter.stopActionMode()
            mode?.finish()
            actionMode = null
        }
    }


    private inner class AdapterAudioInfoListener : ClickListener<AudioInfo> {

        override fun onClick(item: AudioInfo) {
            mainActivityViewModel.audioItemClicked(item)
            findNavController().navigate(PlaylistFragmentDirections.actionPlaylistFragmentToAudioPlayerFragment())
        }

        override fun onActiveModeClick() {
            val selectedItemsCount = playlistAdapter.selectedItems.size
            if (selectedItemsCount != 0) {
                actionMode?.title =
                    getString(R.string.selected_items, selectedItemsCount)
            } else {
                playlistAdapter.stopActionMode()
                actionMode?.finish()
                actionMode = null
            }
        }

        override fun onLongClick(item: AudioInfo) {
            startActionMode()
        }
    }
}

private const val AUDIO_ID_ARG = "com.example.ytaudio.fragments.PlaylistFragment.AUDIO_ID"