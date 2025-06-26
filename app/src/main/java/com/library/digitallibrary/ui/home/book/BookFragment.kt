package com.library.digitallibrary.ui.home.book

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.fragment.findNavController
import com.library.digitallibrary.MainActivity
import com.library.digitallibrary.R
import com.library.digitallibrary.ui.theme.DigitalLibraryTheme

/**
 * The Fragment that hosts the Jetpack Compose UI for the book collection screen.
 */
class BookFragment : Fragment() {

    private val viewModel: BookViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                val books by viewModel.books.collectAsStateWithLifecycle()

                DigitalLibraryTheme {
                    BookScreen(
                        books = books,
                        onBookClick = { book ->
                            // When a book is clicked, navigate to the detail screen.
//                            val action = BookFragmentDirections.actionBookFragmentToDetailFragment(bookId = book.id)
//                            findNavController().navigate(action)
                        }
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Update the main activity's toolbar when this screen is shown.
        val title = getString(R.string.collection_books) // Assumes you have this string resource
        (activity as? MainActivity)?.updateToolbar(MainActivity.ToolbarState.InnerScreen(title))
    }
}