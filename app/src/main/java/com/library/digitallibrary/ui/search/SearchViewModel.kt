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

    val searchQuery = MutableLiveData<String>()

    private val _searchResults = MutableLiveData<List<HomeItem>>()
    val searchResults: LiveData<List<HomeItem>> = _searchResults

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _message = MutableLiveData<String?>()
    val message: LiveData<String?> = _message

    private var searchJob: Job? = null
    private val debouncePeriod: Long = 500 // 500ms delay

    fun onSearchQueryChanged(query: String) {
        searchJob?.cancel() // Cancel the previous search job
        searchJob = viewModelScope.launch {
            if (query.length < 3) {
                // It correctly clears the results and sets a message...
                _isLoading.value = false // THE FIX: Explicitly turn off loading.
                _searchResults.value = emptyList()
                _message.value = getApplication<Application>().getString(R.string.search_prompt_length)

                // ...but then it exits here, leaving _isLoading stuck on 'true' from the last search.
                return@launch
            }

            _isLoading.value = true
            _message.value = null // Clear previous messages

            delay(debouncePeriod) // Wait for the user to stop typing

            try {
                // In a real app, your API would have a search endpoint.
                // We'll simulate it by filtering your mock data.
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