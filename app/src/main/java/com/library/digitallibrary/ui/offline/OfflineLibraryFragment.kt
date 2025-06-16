package com.library.digitallibrary.ui.offline

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.webkit.MimeTypeMap
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.library.digitallibrary.data.adapter.DownloadedAdapter
import com.library.digitallibrary.data.offline.DownloadedItem
import com.library.digitallibrary.databinding.FragmentOfflineLibraryBinding
import com.library.digitallibrary.utils.SwipeToDeleteCallback
import java.io.File
import androidx.core.net.toUri

@Suppress("DEPRECATION")
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

        // Call the new setup function for hiding the keyboard
        setupHideKeyboardOnTap(view)
    }

    private fun setupRecyclerView() {
        downloadedAdapter = DownloadedAdapter { item ->
            openDownloadedFile(item)
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
                            viewModel.restoreItem(itemToDelete)
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
            Log.e("DownloadFlow", "Data: $downloadedItems")
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

    // In OfflineLibraryFragment.kt

    // --- THIS IS THE FINAL, POLISHED FUNCTION TO OPEN FILES ---
    private fun openDownloadedFile(item: DownloadedItem) {
        val filePath = item.localFilePath
        if (filePath.isNullOrBlank()) {
            Toast.makeText(
                requireContext(),
                "File not found or path is invalid.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        try {
            // Create a file object from the path saved in our database
            val file = File(filePath.toUri().path!!)
            if (!file.exists()) {
                Toast.makeText(
                    requireContext(),
                    "Error: The downloaded file does not exist.",
                    Toast.LENGTH_SHORT
                ).show()
                return
            }

            // Get a secure content URI using the FileProvider. This is required for security.
            val contentUri = FileProvider.getUriForFile(
                requireContext(),
                "${requireContext().packageName}.provider",
                file
            )

            val intent = Intent(Intent.ACTION_VIEW)
            // Dynamically get the MIME type (e.g., "application/pdf", "video/mp4") from the file extension
            val mimeType = getMimeType(file)
            intent.setDataAndType(contentUri, mimeType)
            // Grant permission for the receiving app to read our file
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

            startActivity(intent)

        } catch (e: ActivityNotFoundException) {
            // This error happens if the user has NO app installed that can open this file type
            Toast.makeText(requireContext(), "${e.message}", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            // This catches any other errors (e.g., FileProvider issues, invalid paths)
            Toast.makeText(requireContext(), "${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    // Helper function to get the MIME type from a file
    private fun getMimeType(file: File): String? {
        val extension = file.extension
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
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


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}