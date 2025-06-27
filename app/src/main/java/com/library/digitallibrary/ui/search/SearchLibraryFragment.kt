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
        setupHideKeyboardOnTap(view)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupHideKeyboardOnTap(view: View) {
        if (view !is EditText) {
            view.setOnTouchListener { _, _ ->
                hideSoftKeyboard()
                false
            }
        }
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                val innerView = view.getChildAt(i)
                setupHideKeyboardOnTap(innerView)
            }
        }
    }

    private fun hideSoftKeyboard() {
        activity?.let { act ->
            val inputMethodManager = act.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            act.currentFocus?.let { focusedView ->
                inputMethodManager.hideSoftInputFromWindow(focusedView.windowToken, 0)
                focusedView.clearFocus()
            }
        }
    }

    private fun setupRecyclerView() {
        resultsAdapter = MixedContentAdapter(object: MixedContentAdapter.ItemClickListener{
            override fun onBookClicked(book: Book) {
                // TODO: Navigate to book details
                Toast.makeText(requireContext(),"Book Clicked: ${book.title}", Toast.LENGTH_SHORT).show()
            }

            override fun onVideoClicked(video: Video) {
                // TODO: Navigate to video details
                Toast.makeText(requireContext(),"Video Clicked: ${video.title}", Toast.LENGTH_SHORT).show()
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
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Trigger search as user types
                viewModel.onSearchQueryChanged(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun observeViewModel() {
        viewModel.searchResults.observe(viewLifecycleOwner) { results ->
            resultsAdapter.submitList(results)
            // Only show recycler view if there are results
            binding.searchResultsRecyclerView.visibility = if (results.isNotEmpty()) View.VISIBLE else View.GONE
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.message.observe(viewLifecycleOwner) { message ->
            if (message != null) {
                // If there's a message, show the empty state container and set the text
                binding.emptyStateContainer.visibility = View.VISIBLE
                binding.messageTextView.text = message
            } else {
                // If there's no message (i.e., we have results), hide the container
                binding.emptyStateContainer.visibility = View.GONE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}