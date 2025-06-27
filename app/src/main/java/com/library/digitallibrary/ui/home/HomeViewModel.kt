package com.library.digitallibrary.ui.home

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.library.digitallibrary.R
import com.library.digitallibrary.data.models.ads.Ads
import com.library.digitallibrary.data.models.home.HomeItem
import com.library.digitallibrary.data.models.home.HomeScreenItem
import com.library.digitallibrary.data.retrofit.RetrofitClient
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val apiService = RetrofitClient.create(application, useMock = true)


    private val _screenItems = MutableLiveData<List<HomeScreenItem>>()
    val screenItems: LiveData<List<HomeScreenItem>> = _screenItems


    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    init {
        fetchHomeScreenData()
    }


    private fun fetchHomeScreenData() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                // Fetch all data sources
                val books = apiService.mockBooks()
                val videos = apiService.mockVideos()
                val ads = listOf(
                    Ads(
                        1,
                        "Visit Our Sponsor",
                        imageResId = R.drawable.ic_slide,
                        url = "https://www.google.com"
                    ),
                    Ads(
                        2,
                        "Special Offer",
                        imageResId = R.drawable.ic_slide,
                        url = "https://www.android.com"
                    ),
                    Ads(
                        3,
                        "Learn More",
                        imageResId = R.drawable.ic_slide,
                        url = "https://developer.android.com"
                    )
                )
                val cardItems = listOf(
                    Ads(
                        id = 1,
                        imageResId = R.drawable.ic_group_click,
                        titleResId = R.string.collection_videos
                    ),
                    Ads(
                        id = 2,
                        imageResId = R.drawable.ic_text_books,
                        titleResId = R.string.collection_books
                    )
                )
                // In a real app, your "top 5" logic would be here
                val top5Mixed =
                    (books.map { HomeItem.BookItem(it) } + videos.map { HomeItem.VideoItem(it) })
                        .sortedByDescending { it.getTimestamp() }.take(5)

                // Build the single list for the screen in the correct order

                val items = mutableListOf<HomeScreenItem>()
                items.add(HomeScreenItem.AdsSection(ads))
                items.add(HomeScreenItem.CardItemSection(cardItems))
                items.add(
                    HomeScreenItem.TitledMixedSection(
                        titleResId = R.string.recent_announcements,
                        items = top5Mixed,
                        categoryId = "new_releases" // Unique ID for this category
                    )
                )
                items.add(
                    HomeScreenItem.TitledVideoSection(
                        titleResId = R.string.collection_videos,
                        videos = videos,
                        categoryId = "trending_videos" // Unique ID for this category
                    )
                )
                items.add(
                    HomeScreenItem.TitledBookSection(
                        titleResId = R.string.collection_books,
                        books = books,
                        categoryId = "featured_books" // Unique ID for this category
                    )
                )
                items.add(HomeScreenItem.Copyright)

                // --- ADD THIS LOGGING BLOCK ---
                Log.d("ViewModelCheck", "--- Final list being sent to UI ---")
                items.forEachIndexed { index, homeScreenItem ->
                    Log.d(
                        "ViewModelCheck",
                        "Item $index is type: ${homeScreenItem.javaClass.simpleName}"
                    )
                }
                _screenItems.value = items

            } catch (e: Exception) {
                _error.value = "Fail to load: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}