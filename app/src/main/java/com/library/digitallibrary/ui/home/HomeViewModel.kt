package com.library.digitallibrary.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.library.digitallibrary.R
import com.library.digitallibrary.data.models.ads.Ads
import com.library.digitallibrary.data.models.book.Book
import com.library.digitallibrary.data.models.home.HomeItem
import com.library.digitallibrary.data.models.video.Video
import com.library.digitallibrary.data.retrofit.RetrofitClient
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val apiService = RetrofitClient.create(application, useMock = true)

    private val _books = MutableLiveData<List<Book>>()
    val books: LiveData<List<Book>> = _books

    private val _items = MutableLiveData<List<HomeItem>>()
    val items: LiveData<List<HomeItem>> = _items

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
        fetchData()
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
                val data = mockCardItems()
                _cardItem.value = data
            } catch (e: Exception) {
                _error.value = "Fail to load: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun fetchData() {
        _isLoading.value = true
        _error.value = null
        viewModelScope.launch {
            try {
                val books = apiService.mockBooks()
                val videos = apiService.mockVideos()

                _books.value = books

                val combinedList = mutableListOf<HomeItem>()
                combinedList.addAll(books.map { HomeItem.BookItem(it) })
                combinedList.addAll(videos.map { HomeItem.VideoItem(it) })

                _items.value = combinedList

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
                id = 1,
                imageResId = R.drawable.card_video,
                titleResId = R.string.collection_videos  // Resource ID
            ),
            Ads(
                id = 2,
                imageResId = R.drawable.card_book,
                titleResId = R.string.collection_books  // Resource ID
            )
        )
    }

    private fun mockAds(): List<Ads> {
        return listOf(
            Ads(1, "Ad 1", imageResId = R.drawable.ic_slide, "ad 1"),
            Ads(2, "Ad 2", imageResId = R.drawable.ic_slide, "ad 2"),
            Ads(3, "Ad 3", imageResId = R.drawable.ic_slide, "ad 3")
        )
    }
}