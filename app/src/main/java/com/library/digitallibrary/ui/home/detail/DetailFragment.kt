package com.library.digitallibrary.ui.home.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

/**
 * A Fragment responsible for displaying the detailed information for a single item,
 * which can be either a Book or a Video.
 *
 * It receives an item ID and type via Safe Args from the navigation component. It uses a
 * [DetailViewModel] to fetch and observe the item's details and its download status.
 * The UI is dynamically updated based on the data received from the ViewModel.
 */
class DetailFragment : Fragment() {
    // View binding instance to safely access views. It's nullable to handle the view's lifecycle.
    private var _binding: FragmentDetailBinding? = null
    // Non-nullable accessor for the binding, valid only between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    private lateinit var viewModel: DetailViewModel
    // Safe Args delegate to easily access navigation arguments passed to this fragment.
    private val args: DetailFragmentArgs by navArgs()

    /**
     * Inflates the fragment's layout.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    /**
     * Called after the view has been created. This is where view initialization and
     * data loading should happen.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[DetailViewModel::class.java]

        // Check the navigation arguments to determine which item to load (Book or Video).
        // This triggers the data loading process in the ViewModel.
        if (args.bookId != -1) {
            viewModel.loadItemDetails(args.bookId, "BOOK")
        } else if (args.videoId != -1) {
            viewModel.loadItemDetails(args.videoId, "VIDEO")
        }

        observeViewModel()
    }

    /**
     * Sets up observers on the ViewModel's LiveData objects.
     * The UI will automatically update whenever the observed data changes.
     */
    private fun observeViewModel() {
        // Observe book details. When data arrives, bind it to the UI.
        viewModel.bookDetails.observe(viewLifecycleOwner) { book ->
            book?.let { bindBookData(it) }
        }

        // Observe video details. When data arrives, bind it to the UI.
        viewModel.videoDetails.observe(viewLifecycleOwner) { video ->
            video?.let { bindVideoData(it) }
        }

        // Observe the download status and update the download button's state accordingly.
        viewModel.downloadStatus.observe(viewLifecycleOwner) { status ->
            updateDownloadButtonState(status)
        }
    }

    /**
     * Binds Book data to the corresponding views in the layout.
     * @param book The [Book] object containing the details to display.
     */
    private fun bindBookData(book: Book) {
        updateImageAspectRatio("2:3") // Use a taller aspect ratio for book covers.

        binding.detailTitle.text = book.title
        binding.detailAuthor.text = book.author

        // Display tags if they exist.
        if (book.tags.isNotEmpty()) {
            binding.detailTag.text = "Tags: ${book.tags.joinToString(", ")}"
            binding.detailTag.visibility = View.VISIBLE
        } else {
            binding.detailTag.visibility = View.GONE
        }

        // Set the availability status text and color.
        if (book.isAvailable) {
            binding.detailStatus.setText(R.string.status_available)
            binding.detailStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.status_available_green))
        } else {
            binding.detailStatus.setText(R.string.status_not_available)
            binding.detailStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.status_unavailable_red))
        }

        // Load the book's thumbnail image using Glide.
        Glide.with(this).load(book.thumbnail).placeholder(R.drawable.placeholder_image)
            .into(binding.detailImage)

        // Set up the download button to start a download when clicked.
        binding.downloadButton.setOnClickListener {
            Downloader.startDownload(requireContext(), book)
        }
    }

    /**
     * Binds Video data to the corresponding views in the layout.
     * @param video The [Video] object containing the details to display.
     */
    private fun bindVideoData(video: Video) {
        updateImageAspectRatio("16:9") // Use a widescreen aspect ratio for video thumbnails.

        binding.detailTitle.text = video.title
        binding.detailAuthor.text = video.author
        binding.detailStatus.visibility = View.GONE // Status is not applicable for videos.
        binding.detailTag.text = "Duration: ${video.duration}" // Use the tag view for duration.
        binding.detailTag.visibility = View.VISIBLE

        Glide.with(this).load(video.thumbnailUrl).placeholder(R.drawable.placeholder_image)
            .into(binding.detailImage)

        binding.downloadButton.setOnClickListener {
            Downloader.startDownload(requireContext(), video)
        }
    }

    /**
     * Dynamically changes the aspect ratio of the main image view.
     * This is useful for accommodating both book covers (tall) and video thumbnails (wide).
     * @param ratio A string representing the dimension ratio, e.g., "16:9" or "2:3".
     */
    private fun updateImageAspectRatio(ratio: String) {
        val constraintSet = ConstraintSet()
        constraintSet.clone(binding.detailConstraintLayout)
        constraintSet.setDimensionRatio(binding.detailImage.id, ratio)
        constraintSet.applyTo(binding.detailConstraintLayout)
    }

    /**
     * Updates the visibility of the download button, progress indicator, and completed icon
     * based on the current download status.
     * @param status The download status string (e.g., "DOWNLOADING", "COMPLETE").
     */
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
            else -> { // Not downloaded, null, or failed
                binding.downloadButton.visibility = View.VISIBLE
                binding.downloadProgress.visibility = View.GONE
                binding.downloadCompleteIcon.visibility = View.GONE
            }
        }
    }

    /**
     * Called when the fragment is resumed. It requests the MainActivity to update the
     * global toolbar to the "DetailScreen" state, showing a back arrow and title.
     */
    override fun onResume() {
        super.onResume()
        val title = if (args.bookId != -1) "Book Details" else if (args.videoId != -1) "Video Details" else "Details"
        (activity as? MainActivity)?.updateToolbar(MainActivity.ToolbarState.InnerScreen(title))
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
