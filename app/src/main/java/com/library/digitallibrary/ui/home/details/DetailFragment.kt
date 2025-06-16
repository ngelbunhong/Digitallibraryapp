package com.library.digitallibrary.ui.home.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
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
    private val args: DetailFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[DetailViewModel::class.java]

        // Based on the navigation arguments, make ONE call to the ViewModel to start loading everything.
        if (args.bookId != -1) {
            viewModel.loadItemDetails(args.bookId, "BOOK")
        } else if (args.videoId != -1) {
            viewModel.loadItemDetails(args.videoId, "VIDEO")
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.bookDetails.observe(viewLifecycleOwner) { book ->
            book?.let { bindBookData(it) }
        }

        viewModel.videoDetails.observe(viewLifecycleOwner) { video ->
            video?.let { bindVideoData(it) }
        }

        viewModel.downloadStatus.observe(viewLifecycleOwner) { status ->
            updateDownloadButtonState(status)
        }
    }

    private fun bindBookData(book: Book) {
        updateImageAspectRatio("2:3") // Set aspect ratio for a tall book cover

        // --- CORRECTED ---
        binding.detailTitle.text = book.title
        binding.detailAuthor.text = book.author // Use the correct TextView for the author

        // Use the 'detail_tag' TextView for tags
        if (book.tags.isNotEmpty()) {
            binding.detailTag.text = "Tags: ${book.tags.joinToString(", ")}"
            binding.detailTag.visibility = View.VISIBLE
        } else {
            binding.detailTag.visibility = View.GONE
        }

        // --- IMPROVED ---
        // Set text and color for availability status
        if (book.isAvailable) {
            binding.detailStatus.setText(R.string.status_available)
            binding.detailStatus.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.status_available_green
                )
            )
        } else {
            binding.detailStatus.setText(R.string.status_not_available)
            binding.detailStatus.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.status_unavailable_red
                )
            )
        }

        Glide.with(this).load(book.thumbnail).placeholder(R.drawable.placeholder_image)
            .into(binding.detailImage)

        binding.downloadButton.setOnClickListener {
            Downloader.startDownload(
                requireContext(),
                book
            )
        }
    }

    private fun bindVideoData(video: Video) {
        updateImageAspectRatio("16:9") // Set aspect ratio for a wide video thumbnail

        // --- COMPLETED ---
        binding.detailTitle.text = video.title
        binding.detailAuthor.text = video.author
        binding.detailStatus.visibility =
            View.GONE // Hide status view for videos (or set as needed)
        binding.detailTag.text = "Duration: ${video.duration}" // Use tag view for duration
        binding.detailTag.visibility = View.VISIBLE

        Glide.with(this).load(video.thumbnailUrl).placeholder(R.drawable.placeholder_image)
            .into(binding.detailImage)

        // Set the download listener for videos
        binding.downloadButton.setOnClickListener {
            Downloader.startDownload(requireContext(), video)
        }
    }

    private fun updateImageAspectRatio(ratio: String) {
        val constraintSet = ConstraintSet()
        constraintSet.clone(binding.detailConstraintLayout)
        constraintSet.setDimensionRatio(binding.detailImage.id, ratio)
        constraintSet.applyTo(binding.detailConstraintLayout)
    }

    private fun updateDownloadButtonState(status: String?) {
        when (status) {
            "DOWNLOADING" -> {
                binding.downloadButton.visibility = View.INVISIBLE
                binding.downloadProgress.visibility = View.VISIBLE
                binding.downloadCompleteIcon.visibility = View.GONE
            }

            "COMPLETE" -> {
                binding.downloadButton.visibility = View.INVISIBLE
                binding.downloadProgress.visibility = View.GONE
                binding.downloadCompleteIcon.visibility = View.VISIBLE
            }

            else -> { // Not downloaded or failed
                binding.downloadButton.visibility = View.VISIBLE
                binding.downloadProgress.visibility = View.GONE
                binding.downloadCompleteIcon.visibility = View.GONE
            }
        }
    }

    override fun onResume() {
        super.onResume()

        val title =
            if (args.bookId != -1) "Book Details" else if (args.videoId != -1) "Video Details" else "Details"
        (activity as? MainActivity)?.updateToolbar(MainActivity.ToolbarState.DetailScreen(title))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}