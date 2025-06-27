package com.library.digitallibrary.ui.search

import android.app.Application
import androidx.lifecycle.*
import com.library.digitallibrary.R
import com.library.digitallibrary.data.models.home.HomeItem
import com.library.digitallibrary.data.retrofit.RetrofitClient
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchViewModel(application: Application) : AndroidViewModel(application) {
    private val apiService = RetrofitClient.create(application, useMock = true)

    private val _searchResults = MutableLiveData<List<HomeItem>>()
    val searchResults: LiveData<List<HomeItem>> = _searchResults

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _message = MutableLiveData<String?>()
    val message: LiveData<String?> = _message

    private var searchJob: Job? = null
    private val debouncePeriod: Long = 500 // 500ms delay

    init {
        // Set the initial state when the ViewModel is created
        _isLoading.value = false
        _searchResults.value = emptyList()
        _message.value = getApplication<Application>().getString(R.string.search_prompt)
    }

    fun onSearchQueryChanged(query: String) {
        searchJob?.cancel() // Cancel the previous search job
        searchJob = viewModelScope.launch {
            if (query.length < 2) {
                _isLoading.value = false
                _searchResults.value = emptyList()
                // Only show length prompt if the user has typed something
                if (query.isNotEmpty()) {
                    _message.value = getApplication<Application>().getString(R.string.search_prompt_length)
                } else {
                    _message.value = getApplication<Application>().getString(R.string.no_downloads_yet)
                }
                return@launch
            }

            _isLoading.value = true
            _message.value = null // Clear previous messages while searching
            _searchResults.value = emptyList() // Clear previous results

            delay(debouncePeriod) // Wait for the user to stop typing

            try {
                // Simulate API call
                val books = apiService.mockBooks().filter {
                    it.title.contains(query, ignoreCase = true) || it.author.contains(query, ignoreCase = true)
                }
                val videos = apiService.mockVideos().filter {
                    it.title.contains(query, ignoreCase = true) || it.author.contains(query, ignoreCase = true)
                }

                val results = (books.map { HomeItem.BookItem(it) } + videos.map { HomeItem.VideoItem(it) })
                    .sortedByDescending { it.getTimestamp() }

                _searchResults.value = results
                if (results.isEmpty()) {
                    // Set message if the search yields no results
                    _message.value = getApplication<Application>().getString(R.string.no_results_found)
                }

            } catch (e: Exception) {
                _message.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}