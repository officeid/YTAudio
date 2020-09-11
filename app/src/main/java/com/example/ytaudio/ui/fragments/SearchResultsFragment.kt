package com.example.ytaudio.ui.fragments

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.paging.ExperimentalPagingApi
import androidx.recyclerview.widget.ItemTouchHelper
import com.example.ytaudio.R
import com.example.ytaudio.databinding.FragmentSearchResultsBinding
import com.example.ytaudio.di.Injectable
import com.example.ytaudio.ui.adapters.ReboundingSwipeActionCallback
import com.example.ytaudio.ui.adapters.YTItemAdapter
import com.example.ytaudio.ui.adapters.YTItemListener
import com.example.ytaudio.ui.adapters.YTLoadStateAdapter
import com.example.ytaudio.ui.viewmodels.SearchResultsViewModel
import com.example.ytaudio.vo.YouTubeItem
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject


@ExperimentalPagingApi
class SearchResultsFragment : Fragment(), YTItemListener, Injectable {

    @Inject
    lateinit var searchResultsViewModelFactory: ViewModelProvider.Factory

    private val searchResultsViewModel: SearchResultsViewModel by viewModels {
        searchResultsViewModelFactory
    }

    private lateinit var binding: FragmentSearchResultsBinding
    private var job: Job? = null
    private val navArgs: SearchResultsFragmentArgs by navArgs()
    private val youtubeItemsAdapter = YTItemAdapter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(
            this, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().navigate(SearchResultsFragmentDirections.actionSearchResultsFragmentToYouTubeFragment())
                }
            })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchResultsBinding.inflate(inflater).apply {
            itemYoutubeView.run {
                ItemTouchHelper(ReboundingSwipeActionCallback()).attachToRecyclerView(this)
                adapter = youtubeItemsAdapter.withLoadStateFooter(YTLoadStateAdapter())
            }

            searchToolbar.setNavigationOnClickListener {
                findNavController().navigate(SearchResultsFragmentDirections.actionSearchResultsFragmentToYouTubeFragment())
            }

            searchQuery.run {
                setText(navArgs.query)
                setOnClickListener {
                    findNavController().navigate(
                        SearchResultsFragmentDirections.actionSearchResultsFragmentToSearchFragment(
                            (it as EditText).text.toString()
                        )
                    )
                }
            }
            lifecycleOwner = this@SearchResultsFragment
        }

        searchResultsViewModel.errorEvent.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }
        setFromQuery()
    }

    override fun onItemClicked(cardView: View, item: YouTubeItem) {
        startYouTubeIntent(item.videoId)
    }

    override fun onItemLongClicked(item: YouTubeItem): Boolean {
        MenuBottomSheetDialogFragment(R.menu.youtube_bottom_sheet_menu) {
            when (it.itemId) {
                R.id.menu_open_youtube -> {
                    startYouTubeIntent(item.videoId)
                    true
                }
                R.id.menu_add -> {
                    searchResultsViewModel.addToPlaylist(item.videoId)
                    true
                }
                else -> false
            }
        }.show(parentFragmentManager, null)
        return true
    }

    override fun onItemIconChanged(item: YouTubeItem, newValue: Boolean) {
        item.isAdded = newValue
        if (newValue) {
            searchResultsViewModel.addToPlaylist(item.videoId)
        } else {
            searchResultsViewModel.deleteFromPlaylist(item.videoId)
        }
    }

    private fun startYouTubeIntent(id: String) {
        try {
            context?.startActivity(
                Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:$id"))
            )
        } catch (e: ActivityNotFoundException) {
            context?.startActivity(
                Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=$id"))
            )
        }
    }

    private fun setFromQuery() {
        job?.cancel()
        job = lifecycleScope.launch {
            searchResultsViewModel.getFromQuery(navArgs.query).collectLatest {
                youtubeItemsAdapter.submitData(it)
            }
        }
    }
}