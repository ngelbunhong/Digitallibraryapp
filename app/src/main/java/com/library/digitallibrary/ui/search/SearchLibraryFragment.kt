package com.library.digitallibrary.ui.search

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.library.digitallibrary.MainActivity
import com.library.digitallibrary.data.adapter.MixedContentAdapter
import com.library.digitallibrary.data.models.book.Book
import com.library.digitallibrary.data.models.video.Video
import com.library.digitallibrary.databinding.FragmentSearchLibraryBinding


class SearchLibraryFragment : Fragment() {
    private var _binding: FragmentSearchLibraryBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: SearchViewModel
    private lateinit var resultsAdapter: MixedContentAdapter


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchLibraryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[SearchViewModel::class.java]

        setupRecyclerView()
        setupSearchInput()
        observeViewModel()

        // Call the new setup function for hiding the keyboard
        setupHideKeyboardOnTap(view)
    }


    // --- NEW FUNCTION TO HIDE KEYBOARD ON TAP ---
    @SuppressLint("ClickableViewAccessibility")
    private fun setupHideKeyboardOnTap(view: View) {
        // Set up a listener on the parent layout.
        // If the view is not an EditText, set a touch listener to hide the keyboard.
        if (view !is EditText) {
            view.setOnTouchListener { _, _ ->
                hideSoftKeyboard()
                false // Return false so the touch event is not consumed and can be passed to other views.
            }
        }

        // If the view is a container, recursively apply the listener to its children.
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                val innerView = view.getChildAt(i)
                setupHideKeyboardOnTap(innerView)
            }
        }
    }

    // --- NEW HELPER FUNCTION TO HIDE THE KEYBOARD ---
    private fun hideSoftKeyboard() {
        activity?.let { act ->
            val inputMethodManager = act.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            act.currentFocus?.let { focusedView ->
                inputMethodManager.hideSoftInputFromWindow(focusedView.windowToken, 0)
                focusedView.clearFocus() // Optional: clear focus from the EditText
            }
        }
    }

    private fun setupRecyclerView() {
        resultsAdapter = MixedContentAdapter(object: MixedContentAdapter.ItemClickListener{
            override fun onBookClicked(book: Book) {
                Toast.makeText(requireContext(),"Item $book", Toast.LENGTH_SHORT).show()
            }

            override fun onVideoClicked(video: Video) {
                Toast.makeText(requireContext(),"Item $video", Toast.LENGTH_SHORT).show()
            }
        })
        val spanCount = if ((resources.configuration.smallestScreenWidthDp >= 600)) 3 else 2
        binding.searchResultsRecyclerView.apply {
            layoutManager = GridLayoutManager(context, spanCount)
            adapter = resultsAdapter
        }
    }

    private fun setupSearchInput(){
        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                viewModel.onSearchQueryChanged(s.toString())
            }
        })
    }

    private fun observeViewModel() {
        viewModel.searchResults.observe(viewLifecycleOwner) { results ->
            resultsAdapter.submitList(results)
            // Show/hide the main list based on whether there are results
            binding.searchResultsRecyclerView.visibility = if (results.isNotEmpty()) View.VISIBLE else View.GONE
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.message.observe(viewLifecycleOwner) { message ->
            if (message != null) {
                binding.messageTextView.visibility = View.VISIBLE
                binding.messageTextView.text = message
            } else {
                binding.messageTextView.visibility = View.GONE
            }
        }
    }

    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}