package com.library.digitallibrary.ui.home.book

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.library.digitallibrary.data.models.book.Book
import com.library.digitallibrary.data.retrofit.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * The ViewModel responsible for providing data to the BookFragment.
 *
 * It fetches the list of all available books from the data source and exposes it
 * to the UI layer for display.
 */
class BookViewModel(application: Application) : AndroidViewModel(application) {

    private val apiService = RetrofitClient.create(application, useMock = true)

    // Private StateFlow for internal state management.
    private val _books = MutableStateFlow<List<Book>>(emptyList())
    // Public, read-only StateFlow for the UI to observe.
    val books: StateFlow<List<Book>> = _books

    init {
        loadAllBooks()
    }

    /**
     * Fetches all books from the API service and updates the StateFlow.
     */
    private fun loadAllBooks() {
        viewModelScope.launch {
            try {
                // Call the API endpoint to get the list of books.
                val bookList = apiService.mockBooks()
                _books.value = bookList
            } catch (e: Exception) {
                // In a real app, handle errors properly here.
                _books.value = emptyList()
            }
        }
    }
}
