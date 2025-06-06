package com.library.digitallibrary.ui.home.details

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.library.digitallibrary.MainActivity
import com.library.digitallibrary.databinding.FragmentDetailBinding

class DetailFragment : Fragment() {
    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!
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
        // Check which ID was passed and load the correct data
        if (args.bookId != -1) {
            // Load book details using args.bookId
            // viewModel.loadBook(args.bookId)
            // Example of direct binding:
            binding.itemTitle.text = "Book Title for ID: ${args.bookId}"
            binding.itemAuthor.visibility = View.VISIBLE // Show book-specific view
        } else if (args.videoId != -1) {
            // Load video details using args.videoId
            // viewModel.loadVideo(args.videoId)
            // Example of direct binding:
            binding.itemTitle.text = "Video Title for ID: ${args.videoId}"
            binding.itemAuthor.visibility = View.VISIBLE // Show video-specific view
        }
    }


    override fun onResume() {
        super.onResume()
        val title = if (args.bookId != -1) "Book Details" else if (args.videoId != -1) "Video Details" else "Details"

        (activity as? MainActivity)?.updateToolbar(MainActivity.ToolbarState.DetailScreen(title))

    }




    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}