package com.library.digitallibrary.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.library.digitallibrary.R
import com.library.digitallibrary.data.local.repository.AdRepository
import com.library.digitallibrary.data.models.ads.Ads
import com.library.digitallibrary.data.models.book.Book
import com.library.digitallibrary.data.retrofit.RetrofitClient
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val apiService = RetrofitClient.create(application, useMock = true)
    private val adRepository = AdRepository(apiService)  // Initialize AdRepository with apiService


    private val _book = MutableLiveData<List<Book>>()
    val book: LiveData<List<Book>> get() = _book

    private val _ads = MutableLiveData<List<Ads>>()
    val ads: LiveData<List<Ads>> get() = _ads

    private val _cardItem = MutableLiveData<List<Ads>>()
    val cardItem: LiveData<List<Ads>> get() = _cardItem


    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    init {
        fetchAds()
        fetchCardItem()
    }

    private fun fetchAds() {
        _isLoading.value = true
        _error.value = null
        viewModelScope.launch {
            try {
                val adList = mockAds()
                _ads.value = adList
            } catch (e: Exception) {
                _error.value = "Fail to load: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun fetchCardItem() {
        _isLoading.value = true
        _error.value = null
        viewModelScope.launch {
            try {
                val cardItem = mockCardItems()
                _cardItem.value = cardItem
            } catch (e: Exception) {
                _error.value = "Fail to load: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun mockCardItems(): List<Ads> {
        return listOf(
            Ads(
                1,
                imageResId = R.drawable.card_video,
                title = R.string.collection_videos.toString()
            ),
            Ads(2, imageResId = R.drawable.card_book, title = R.string.collection_books.toString()),
        )
    }

    private fun mockAds(): List<Ads> {
        return listOf(
            Ads(1, "Ad 1", imageResId = R.drawable.ic_slide, "ad 1"),
            Ads(2, "Ad 2", imageResId = R.drawable.ic_slide, "ad 2"),
            Ads(3, "Ad 3", imageResId = R.drawable.ic_slide, "ad 3")
        )
    }


    private fun loadBooks() {
        _isLoading.value = true
        _error.value = null
        viewModelScope.launch {
            try {
                val books = apiService.mockBooks()
                _book.value = books
            } catch (e: Exception) {
                _error.value = "Fail to load: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}