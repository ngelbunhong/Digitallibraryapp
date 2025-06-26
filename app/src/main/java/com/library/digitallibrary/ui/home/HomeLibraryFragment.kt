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

/**
 * The main screen of the application, displaying a dynamic list of content sections.
 *
 * This fragment is responsible for:
 * - Hosting the main RecyclerView that displays various sections like ads, books, and videos.
 * - Initializing and connecting the [HomeViewModel] to observe home screen data.
 * - Setting up the [HomeAdapter] and handling all user interactions from it, such as clicks
 * on books, videos, ads, or "see more" buttons.
 * - Navigating to other parts of the app, like the detail screen, using the Jetpack Navigation component.
 * - Launching URLs in a Chrome Custom Tab for a seamless web browsing experience.
 * - Updating the main activity's toolbar based on the device type (phone vs. tablet).
 */
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

        // Create the adapter, providing an implementation for the listener interface
        // to handle all click events delegated from the adapter's view holders.
        val homeAdapter = HomeAdapter(object : HomeAdapter.HomeAdapterListener {
            override fun onSeeMoreClicked(sectionTitle: String) {
                // TODO: Implement navigation to a full list screen for the given section.
                Toast.makeText(requireContext(), "See More for $sectionTitle", Toast.LENGTH_SHORT)
                    .show()
            }

            override fun onBookItemClicked(book: Book) {
                navigateToBookDetails(book.id)
            }

            override fun onVideoItemClicked(video: Video) {
                navigateToVideoDetails(video.id)
            }

            override fun onAdClicked(ad: Ads) {
                openUrlInCustomTab(ad.url)
            }

            override fun onCardItemClicked(cardItem: Ads) {
                // TODO: Implement navigation for card items.
                // This is a placeholder for future navigation logic based on which card is tapped.
                when (cardItem.titleResId) {
                    R.string.collection_videos -> {
                        findNavController().navigate(R.id.action_home_to_video)
                    }

                    R.string.collection_books -> {
                        findNavController().navigate(R.id.action_home_to_book)
                    }
                }
            }
        })

        // Setup the RecyclerView
        binding.mainRecyclerView.adapter = homeAdapter
        binding.mainRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Observe the list of home screen items from the ViewModel and submit it to the adapter.
        viewModel.screenItems.observe(viewLifecycleOwner) { items ->
            homeAdapter.submitList(items)
        }
    }

    /**
     * Opens a given URL in a Chrome Custom Tab for a better user experience than a standard WebView.
     * It themes the custom tab to match the app's primary color.
     * @param url The URL string to open.
     */
    private fun openUrlInCustomTab(url: String?) {
        if (url.isNullOrBlank()) {
            Toast.makeText(requireContext(), "No link available for this ad.", Toast.LENGTH_SHORT)
                .show()
            return
        }

        try {
            // Define the color scheme for the Custom Tab toolbar.
            val colorSchemeParams = CustomTabColorSchemeParams.Builder()
                .setToolbarColor(ContextCompat.getColor(requireContext(), R.color.primary))
                .build()

            // Build the Custom Tabs Intent.
            val customTabsIntent = CustomTabsIntent.Builder()
                .setDefaultColorSchemeParams(colorSchemeParams)
                .setShowTitle(true) // Show the page title in the toolbar.
                .build()

            // Launch the URL.
            customTabsIntent.launchUrl(requireContext(), url.toUri())

        } catch (e: ActivityNotFoundException) {
            Toast.makeText(
                requireContext(),
                "No web browser found to open the link.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    /**
     * Navigates to the detail screen for a specific book.
     * @param bookId The ID of the book to display.
     */
    private fun navigateToBookDetails(bookId: Int) {
        // Use the generated Safe Args Directions class to create the navigation action.
        val action = HomeLibraryFragmentDirections.actionHomeToDetail(bookId = bookId)
        findNavController().navigate(action)
    }

    /**
     * Navigates to the detail screen for a specific video.
     * @param videoId The ID of the video to display.
     */
    private fun navigateToVideoDetails(videoId: Int) {
        val action = HomeLibraryFragmentDirections.actionHomeToDetail(videoId = videoId)
        findNavController().navigate(action)
    }


    /**
     * Called when the fragment is resumed. It updates the main activity's toolbar
     * to reflect the correct state for the home screen (phone vs. tablet).
     */
    override fun onResume() {
        super.onResume()
        val mainActivity = activity as? MainActivity
        // Check if the device is a tablet and request the appropriate toolbar state.
        if (mainActivity?.isTablet() == true) {
            mainActivity.updateToolbar(MainActivity.ToolbarState.HomeTablet)
        } else {
            mainActivity?.updateToolbar(MainActivity.ToolbarState.HomePhone)
        }
    }

    /**
     * Called when the fragment's view is being destroyed.
     * It's crucial to null out the binding reference here to prevent memory leaks.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
