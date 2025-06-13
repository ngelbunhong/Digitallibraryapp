package com.library.digitallibrary.ui.home

import android.content.ActivityNotFoundException
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.library.digitallibrary.MainActivity
import com.library.digitallibrary.R
import com.library.digitallibrary.data.adapter.HomeAdapter
import com.library.digitallibrary.data.models.ads.Ads
import com.library.digitallibrary.data.models.book.Book
import com.library.digitallibrary.data.models.video.Video
import com.library.digitallibrary.databinding.FragmentHomeLibraryBinding
import androidx.core.net.toUri

class HomeLibraryFragment : Fragment() {
    private var _binding: FragmentHomeLibraryBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: HomeViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeLibraryBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]

        // Create the adapter with the full listener implementation
        val homeAdapter = HomeAdapter(object : HomeAdapter.HomeAdapterListener {
            override fun onSeeMoreClicked(sectionTitle: String) {
                Toast.makeText(requireContext(), "See More for $sectionTitle", Toast.LENGTH_SHORT)
                    .show()
            }

            override fun onBookItemClicked(book: Book) {
                // Navigate to detail screen, passing the book's ID
                navigateToBookDetails(book.id)
            }

            override fun onVideoItemClicked(video: Video) {
                // Navigate to detail screen, passing the video's ID
                navigateToVideoDetails(video.id)
            }

            override fun onAdClicked(ad: Ads) {
                // 1. Get the URL from the ad object.
                val url = ad.url
                if (url.isNullOrBlank()) {
                    Toast.makeText(requireContext(), "No link available for this ad.", Toast.LENGTH_SHORT).show()
                    return
                }

                try {
                    // --- THE NEW AND CORRECT WAY ---

                    // 1. Define the color scheme for the Custom Tab.
                    val colorSchemeParams = CustomTabColorSchemeParams.Builder()
                        .setToolbarColor(ContextCompat.getColor(requireContext(), R.color.primary))
                        .build()

                    // 2. Build the Custom Tabs Intent and apply the new color scheme.
                    val customTabsIntent = CustomTabsIntent.Builder()
                        .setDefaultColorSchemeParams(colorSchemeParams)
                        .setShowTitle(true)
                        .build()

                    // 3. Launch the URL.
                    customTabsIntent.launchUrl(requireContext(), url.toUri())

                } catch (e: ActivityNotFoundException) {
                    Toast.makeText(requireContext(), "No web browser found to open the link.", Toast.LENGTH_LONG).show()
                }
            }

            override fun onCardItemClicked(cardItem: Ads) {
                // Here you can check the ID of the card to navigate
                when (cardItem.titleResId) { // Using titleResId as a unique identifier for these cards
                    R.string.collection_videos -> {
                        Toast.makeText(
                            requireContext(),
                            "Navigating to Videos Section...",
                            Toast.LENGTH_SHORT
                        ).show()
                        // findNavController().navigate(R.id.action_to_video_category)
                    }

                    R.string.collection_books -> {
                        Toast.makeText(
                            requireContext(),
                            "Navigating to Books Section...",
                            Toast.LENGTH_SHORT
                        ).show()
                        // findNavController().navigate(R.id.action_to_book_category)
                    }
                }
            }
        })

        binding.mainRecyclerView.adapter = homeAdapter
        binding.mainRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        viewModel.screenItems.observe(viewLifecycleOwner) { items ->
            homeAdapter.submitList(items)
        }
    }


    private fun navigateToBookDetails(bookId: Int) {
        // Use the generated Directions class to create the action, passing the bookId
        val action = HomeLibraryFragmentDirections.actionHomeToDetail(bookId = bookId)
        findNavController().navigate(action)
    }

    private fun navigateToVideoDetails(videoId: Int) {
        // Use the generated Directions class, this time passing the videoId
        val action = HomeLibraryFragmentDirections.actionHomeToDetail(videoId = videoId)
        findNavController().navigate(action)
    }


    override fun onResume() {
        super.onResume()
        val mainActivity = activity as? MainActivity
        if (mainActivity?.isTablet() == true) {
            mainActivity.updateToolbar(MainActivity.ToolbarState.HomeTablet)
        } else {
            mainActivity?.updateToolbar(MainActivity.ToolbarState.HomePhone)
        }
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}