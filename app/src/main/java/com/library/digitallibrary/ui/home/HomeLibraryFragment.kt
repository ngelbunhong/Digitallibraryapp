package com.library.digitallibrary.ui.home

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.library.digitallibrary.MainActivity
import com.library.digitallibrary.R
import com.library.digitallibrary.data.adapter.AdsAdapter
import com.library.digitallibrary.data.adapter.BookAdapter
import com.library.digitallibrary.data.adapter.CardItemAdapter
import com.library.digitallibrary.data.adapter.MixedContentAdapter
import com.library.digitallibrary.data.adapter.VideoAdapter
import com.library.digitallibrary.data.models.book.Book
import com.library.digitallibrary.data.models.home.HomeItem
import com.library.digitallibrary.data.models.video.Video
import com.library.digitallibrary.databinding.FragmentHomeLibraryBinding

class HomeLibraryFragment : Fragment() {
    private var _binding: FragmentHomeLibraryBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: HomeViewModel
    private lateinit var adsAdapter: AdsAdapter
    private lateinit var cardItemAdapter: CardItemAdapter
    private lateinit var mixedContentAdapter: MixedContentAdapter
    private lateinit var bookAdapter: BookAdapter
    private lateinit var videoAdapter: VideoAdapter


    private val autoScrollHelper = Handler(Looper.getMainLooper())
    private var currentPage = 0
    private val scrollerRunnable = object : Runnable {
        override fun run() {
            val itemCount = adsAdapter.itemCount
            if (itemCount == 0) return
            currentPage = (currentPage + 1) % itemCount
            binding.viewPagerAds.setCurrentItem(currentPage, true)
            autoScrollHelper.postDelayed(this, 5000)
        }

    }

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


        initializeAdapters()
        setupRecyclerViews()
        observeViewModel()
    }

    private fun isTablet(): Boolean {
        return resources.configuration.smallestScreenWidthDp >= 600
    }

    private fun setupRecyclerViews() {

        // --- Get Span Counts from Resources ---
        val videoSpanCount = resources.getInteger(R.integer.video_grid_span_count)

        // You can do the same for collectionSpanCount if you like!
        // For now, let's keep it programmatic to show both techniques can coexist.
        val collectionSpanCount = if (isTablet()) 3 else 2

        // ads adapter
        binding.viewPagerAds.adapter = adsAdapter

        // card adapter
        binding.recyclerCardItem.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerCardItem.adapter = cardItemAdapter

        //Collection Item
        binding.recyclerViewCollection.layoutManager =
            GridLayoutManager(
                requireContext(),
                1,
                RecyclerView.HORIZONTAL,
                false
            ) // Or your 2-column grid for mixed items
        binding.recyclerViewCollection.adapter = mixedContentAdapter

        // video adapter grid
        binding.recyclerViewVideo.layoutManager =
            GridLayoutManager(requireContext(), videoSpanCount) // Explicitly vertical
        binding.recyclerViewVideo.adapter = videoAdapter

        // book adapter
        binding.recyclerViewBook.layoutManager =
            GridLayoutManager(requireContext(), 1, RecyclerView.HORIZONTAL, false)
        binding.recyclerViewBook.adapter = bookAdapter


        // Add these lines to disable internal scrolling on the RecyclerViews
        binding.recyclerCardItem.isNestedScrollingEnabled = false
        binding.recyclerViewCollection.isNestedScrollingEnabled = false
        binding.recyclerViewVideo.isNestedScrollingEnabled = false
        binding.recyclerViewBook.isNestedScrollingEnabled = false


    }

    private fun observeViewModel() {

        //Ads
        viewModel.ads.observe(viewLifecycleOwner) { ads ->
            adsAdapter.submitList(ads)
            setupIndicators(ads.size)
        }

        //Card Collection
        viewModel.cardItem.observe(viewLifecycleOwner) { cardItems ->
            cardItemAdapter.submitList(cardItems)
        }

        //Mix Videos and Boos
        viewModel.items.observe(viewLifecycleOwner) { collection ->
            Log.d(
                "HomeFragment_Observer",
                "Received collection for MixedContentAdapter. Size: ${collection.size}"
            )
            if (collection.isNotEmpty()) {
                collection.forEachIndexed { index, item ->
                    val title =
                        if (item is HomeItem.BookItem) item.book.title else if (item is HomeItem.VideoItem) item.video.title else "Unknown Item"
                    val timestamp = try {
                        item.getTimestamp()
                    } catch (e: Exception) {
                        "No Timestamp"
                    }
                    Log.d(
                        "HomeFragment_Observer",
                        "Item $index: $title, Type: ${item.javaClass.simpleName}, Timestamp: $timestamp"
                    )
                }
            } else {
                Log.d("HomeFragment_Observer", "Received empty collection for MixedContentAdapter.")
            }
            mixedContentAdapter.submitList(collection)
        }

        //Videos
        viewModel.videos.observe(viewLifecycleOwner) { videos ->
            videoAdapter.submitList(videos)
        }

        //Books
        viewModel.books.observe(viewLifecycleOwner) { books ->
            bookAdapter.submitList(books)
        }


    }

    private fun initializeAdapters() {
        adsAdapter = AdsAdapter { ads ->
            Toast.makeText(requireContext(), "Clicked", Toast.LENGTH_SHORT).show()
            Log.e("TAG", "ItemClickData $ads")
        }

        cardItemAdapter = CardItemAdapter { itemClick ->
            Toast.makeText(requireContext(), "Clicked", Toast.LENGTH_SHORT).show()
            Log.e("TAG", "ItemClickData $itemClick")
        }

        mixedContentAdapter = MixedContentAdapter(object : MixedContentAdapter.ItemClickListener {
            override fun onBookClicked(book: Book) {
                navigateToBookDetails(bookId = book.id)
                Toast.makeText(requireContext(), "Clicked Book: ${book.title}", Toast.LENGTH_SHORT)
                    .show()
                Log.d("HomeLibraryFragment", "MixedContent Book Clicked: ${book.title}")
            }

            override fun onVideoClicked(video: Video) {

                navigateToVideoDetails(videoId = video.id)
                Toast.makeText(requireContext(), "Clicked Book: ${video.title}", Toast.LENGTH_SHORT)
                    .show()
                Log.d("HomeLibraryFragment", "MixedContent Book Clicked: ${video.title}")
            }
        })

        bookAdapter = BookAdapter { books ->
            Toast.makeText(requireContext(), "Clicked", Toast.LENGTH_SHORT).show()
            Log.e("TAG", "ItemClickData $books")
        }

        videoAdapter = VideoAdapter { videos ->
            Toast.makeText(requireContext(), "Clicked", Toast.LENGTH_SHORT).show()
            Log.e("TAG", "ItemClickData $videos")
        }
    }

    private fun setupIndicators(size: Int) {
        binding.indicatorLayout.removeAllViews()
        val indicators = Array(size) { ImageView(requireContext()) }

        for (i in indicators.indices) {
            indicators[i].apply {
                setImageResource(R.drawable.circle_selector)
                layoutParams = LinearLayout.LayoutParams(24, 24).apply {
                    marginStart = 8
                    marginEnd = 8
                }
                isSelected = i == 0 //first on is selected
                binding.indicatorLayout.addView(this)
            }
        }

        //page change listener to update selected indicator
        binding.viewPagerAds.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                for (i in 0 until binding.indicatorLayout.childCount) {
                    binding.indicatorLayout.getChildAt(i).isSelected = i == position
                }
            }

        })
    }

    // Create these helper functions for navigation
    private fun navigateToBookDetails(bookId: Int) {
        // Use the generated Directions class to create the action, passing the bookId
        val action = HomeLibraryFragmentDirections.actionHomeToDetail(bookId = bookId)
        findNavController().navigate(action)
    }

    private fun navigateToVideoDetails(videoId: Int) {
        // Use the generated Directions class, this time passing the videoId
        // The bookId will automatically use its default value of -1
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
        autoScrollHelper.postDelayed(scrollerRunnable, 5000)
    }

    override fun onPause() {
        super.onPause()
        autoScrollHelper.removeCallbacks(scrollerRunnable)
    }

    override fun onDestroy() {
        super.onDestroy()
        autoScrollHelper.removeCallbacks(scrollerRunnable)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}