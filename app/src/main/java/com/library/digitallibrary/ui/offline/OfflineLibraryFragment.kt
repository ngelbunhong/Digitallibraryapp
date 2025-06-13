package com.library.digitallibrary.ui.offline

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.library.digitallibrary.data.adapter.DownloadedAdapter
import com.library.digitallibrary.databinding.FragmentOfflineLibraryBinding
import com.library.digitallibrary.utils.SwipeToDeleteCallback

class OfflineLibraryFragment : Fragment() {

    private var _binding: FragmentOfflineLibraryBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: OfflineViewModel
    private lateinit var downloadedAdapter: DownloadedAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOfflineLibraryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[OfflineViewModel::class.java]

        setupRecyclerView()
        setupSearch()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        downloadedAdapter = DownloadedAdapter { item ->
            Toast.makeText(requireContext(), "Opening ${item.title}", Toast.LENGTH_SHORT).show()
            // Your file opening logic
        }

        binding.recyclerView.adapter = downloadedAdapter
        binding.recyclerView.layoutManager = LinearLayoutManager(context)

        val swipeHandler = object : SwipeToDeleteCallback(requireContext()) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val itemToDelete = downloadedAdapter.currentList[position]
                    viewModel.deleteItem(itemToDelete)

                    Snackbar.make(
                        binding.root,
                        "'${itemToDelete.title}' deleted",
                        Snackbar.LENGTH_LONG
                    )
                        .setAction("Undo") {
                            viewModel.restoreItem()
                        }.show()
                }
            }
        }

        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(binding.recyclerView)
    }

    private fun observeViewModel() {
        viewModel.downloads.observe(viewLifecycleOwner) { downloadedItems ->
            downloadedAdapter.submitList(downloadedItems)
            binding.emptyStateLayout.visibility =
                if (downloadedItems.isEmpty()) View.VISIBLE else View.GONE
            binding.recyclerView.visibility =
                if (downloadedItems.isEmpty()) View.GONE else View.VISIBLE
        }
    }

    private fun setupSearch() {
        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                viewModel.setSearchQuery(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}