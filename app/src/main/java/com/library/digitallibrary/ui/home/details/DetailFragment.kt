package com.library.digitallibrary.ui.home.details

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintSet
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.library.digitallibrary.MainActivity
import com.library.digitallibrary.R
import com.library.digitallibrary.data.models.book.Book
import com.library.digitallibrary.data.models.video.Video
import com.library.digitallibrary.databinding.FragmentDetailBinding
import com.library.digitallibrary.utils.Downloader

class DetailFragment : Fragment() {
    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: DetailViewModel

    // Use Safe Args to get arguments
    private val args: DetailFragmentArgs by navArgs()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[DetailViewModel::class.java]

        if (args.bookId != -1) {
            viewModel.loadBookDetails(args.bookId)
        } else if (args.videoId != -1) {
            viewModel.loadVideoDetails(args.videoId)
        }

        observeViewModel()

    }

    private fun observeViewModel() {
        viewModel.bookDetails.observe(viewLifecycleOwner) { book ->
            book?.let { bindBookData(it) }
        }
        viewModel.videoDetails.observe(viewLifecycleOwner) { video ->
            video?.let {
                bindVideoData(it)
            }
        }
    }

    private fun bindBookData(book: Book) {
        // Set the aspect ratio for a book cover (e.g., a tall 2:3 ratio)
        updateImageAspectRatio("2:3")

        // Use the loaded book data to populate your views
        binding.detailTitle.text = book.title
        binding.detailTitle.text = book.author
        // First, check if the list of tags is not empty
        if (book.tags.isNotEmpty()) {
            // Use joinToString() to convert the list into a single string,
            // separated by a comma and a space.
            val tagsString = book.tags.joinToString(separator = ", ")

            // Set the formatted string to the TextView, adding a prefix.
            binding.detailTag.text = "Tags: $tagsString"

            // Make the TextView visible
            binding.detailTag.visibility = View.VISIBLE
        } else {
            // If there are no tags, make sure the TextView is hidden
            binding.detailTag.visibility = View.GONE
        }
        if (book.isAvailable) {
            binding.detailStatus.setText(R.string.status_available)
        } else {
            binding.detailStatus.setText(R.string.status_not_available)
        }

        Glide.with(this)
            .load(book.thumbnail)
            .into(binding.detailImage)

        // Set the click listener for the download button
        binding.downloadButton.setOnClickListener {
            // Call our Downloader helper class with the full book object
            Downloader.startDownload(requireContext(), book)
        }
    }

    private fun bindVideoData(video: Video) {
        // Set the aspect ratio for a video thumbnail (e.g., a wide 16:9 ratio)
        updateImageAspectRatio("16:9")

        binding.detailTitle.text = video.title
        binding.detailAuthor.text = video.author
        // ... bind other video data ...

        Glide.with(this).load(video.thumbnailUrl).into(binding.detailImage)
        // ... set download listener ...
    }

    // This is the new helper function that changes the aspect ratio
    private fun updateImageAspectRatio(ratio: String) {
        // Create a ConstraintSet to start modifying constraints
        val constraintSet = ConstraintSet()
        // Clone the existing constraints from our layout
        constraintSet.clone(binding.detailConstraintLayout)

        // Set the new dimension ratio string on our ImageView
        constraintSet.setDimensionRatio(binding.detailImage.id, ratio)

        // Apply the new constraints back to our layout
        constraintSet.applyTo(binding.detailConstraintLayout)
    }


    override fun onResume() {
        super.onResume()
        val title =
            if (args.bookId != -1) "Book Details" else if (args.videoId != -1) "Video Details" else "Details"
        Log.d(
            "DetailFragment_DEBUG",
            "onResume: Requesting toolbar update for DetailScreen."
        ) // <-- ADD THIS LOG
        (activity as? MainActivity)?.updateToolbar(MainActivity.ToolbarState.DetailScreen(title))
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}