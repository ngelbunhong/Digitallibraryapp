package com.library.digitallibrary.ui.home.video
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.library.digitallibrary.data.models.video.Video
import com.library.digitallibrary.data.retrofit.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * The ViewModel responsible for providing data to the VideoFragment.
 *
 * It fetches the list of all available videos from the data source and exposes it
 * to the UI layer for display.
 */
class VideoViewModel(application: Application) : AndroidViewModel(application) {

    // Initialize the API service. We'll use the mock data source.
    private val apiService = RetrofitClient.create(application, useMock = true)

    // Private MutableStateFlow that holds the current list of videos. This is the internal state.
    private val _videos = MutableStateFlow<List<Video>>(emptyList())

    // Public, immutable StateFlow that the UI will observe.
    // This prevents the UI from being able to modify the list directly.
    val videos: StateFlow<List<Video>> = _videos

    // The init block is called when the ViewModel is first created.
    // This is the perfect place to start loading our data.
    init {
        loadAllVideos()
    }

    /**
     * Fetches all videos from the API service and updates the StateFlow.
     * It runs in a background coroutine managed by viewModelScope.
     */
    private fun loadAllVideos() {
        // viewModelScope is automatically cancelled when the ViewModel is cleared, preventing leaks.
        viewModelScope.launch {
            try {
                // Fetch the list of videos from our mock data source.
                val videoList = apiService.mockVideos()
                // Update the value of the StateFlow. Any UI collecting this flow will now be updated.
                _videos.value = videoList
            } catch (e: Exception) {
                // TODO: In a real app, handle errors gracefully (e.g., show an error message).
                // For now, we'll just leave the list empty.
                _videos.value = emptyList()
            }
        }
    }
}
