package com.library.digitallibrary.ui.home.details

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.library.digitallibrary.data.local.dao.AppDatabase
import com.library.digitallibrary.data.models.book.Book
import com.library.digitallibrary.data.models.video.Video
import com.library.digitallibrary.data.retrofit.RetrofitClient

class DetailViewModel(application: Application) : AndroidViewModel(application) {
    private val apiService = RetrofitClient.create(application, useMock = true)
    private val dao = AppDatabase.getDatabase(application).downloadedItemDao()

    // This is the private trigger that starts all data loading.
    private val itemIdAndType = MutableLiveData<Pair<Int, String>>()

    // This LiveData reactively fetches book details ONLY when the trigger is for a book.
    val bookDetails: LiveData<Book?> = itemIdAndType.switchMap { (id, type) ->
        if (type == "BOOK") {
            // liveData builder automatically handles background threading
            liveData { emit(apiService.mockBooks().find { it.id == id }) }
        } else {
            MutableLiveData(null) // Return null if it's not a book
        }
    }

    // This LiveData reactively fetches video details ONLY when the trigger is for a video.
    val videoDetails: LiveData<Video?> = itemIdAndType.switchMap { (id, type) ->
        if (type == "VIDEO") {
            liveData { emit(apiService.mockVideos().find { it.id == id }) }
        } else {
            MutableLiveData(null)
        }
    }

    // This LiveData reactively observes the download status for the current item from Room.
    val downloadStatus: LiveData<String?> = itemIdAndType.switchMap { (id, type) ->
        dao.getDownloadStatus(id, type)
            .asLiveData()
            .map { status ->
                Log.d("DownloadStatus", "Observed new download status: $status for item ID: $id")
                status // Pass the status along
            }
    }

    /**
     * The ONLY public function the Fragment needs to call to start loading everything.
     */
    fun loadItemDetails(id: Int, itemType: String) {
        // Setting the value of this trigger will automatically update all the LiveData above.
        itemIdAndType.value = id to itemType
    }
}