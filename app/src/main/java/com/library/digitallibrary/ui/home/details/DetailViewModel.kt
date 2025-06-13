package com.library.digitallibrary.ui.home.details

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.library.digitallibrary.data.models.book.Book
import com.library.digitallibrary.data.models.video.Video
import com.library.digitallibrary.data.retrofit.RetrofitClient
import kotlinx.coroutines.launch

class DetailViewModel(application: Application) : AndroidViewModel(application) {
    private val apiService = RetrofitClient.create(application, useMock = true)

    private val _bookDetails = MutableLiveData<Book?>()
    val bookDetails: LiveData<Book?> = _bookDetails

    private val _videoDetails = MutableLiveData<Video?>()
    val videoDetails: LiveData<Video?> = _videoDetails

    // You would add similar LiveData for Video details
    // private val _videoDetails = ...

    fun loadBookDetails(bookId: Int) {
        viewModelScope.launch {
            try {
                // For testing, we find the book from the full mock list.
                // In a real app, this would be a specific API call: apiService.getBookById(bookId)
                val book = apiService.mockBooks().find { it.id == bookId }
                _bookDetails.postValue(book)
            } catch (e: Exception) {
                _bookDetails.postValue(null) // Post null on error
            }
        }
    }

    fun loadVideoDetails(videoId: Int) {
        viewModelScope.launch {
            try {
                val video = apiService.mockVideos().find { it.id == videoId }
                _videoDetails.postValue(video)

            } catch (e: Exception) {
                _videoDetails.postValue(null)
            }
        }
    }
}