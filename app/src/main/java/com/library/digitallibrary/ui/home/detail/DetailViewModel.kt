package com.library.digitallibrary.ui.home.detail

import android.app.Application
import android.app.DownloadManager
import android.content.Context
import android.util.Log
import androidx.lifecycle.*
import com.library.digitallibrary.data.local.dao.AppDatabase
import com.library.digitallibrary.data.models.book.Book
import com.library.digitallibrary.data.models.video.Video
import com.library.digitallibrary.data.retrofit.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DetailViewModel(application: Application) : AndroidViewModel(application) {
    private val apiService = RetrofitClient.create(application, useMock = true)
    private val dao = AppDatabase.getDatabase(application).downloadedItemDao()
    private val downloadManager = application.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

    // This is the private trigger that starts all data loading.
    private val itemIdAndType = MutableLiveData<Pair<Int, String>>()

    // This LiveData reactively fetches book details ONLY when the trigger is for a book.
    val bookDetails: LiveData<Book?> = itemIdAndType.switchMap { (id, type) ->
        if (type == "BOOK") {
            liveData { emit(apiService.mockBooks().find { it.id == id }) }
        } else {
            MutableLiveData(null)
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

    // This LiveData reactively observes the download status and VERIFIES it.
    val downloadStatus: LiveData<String?> = itemIdAndType.switchMap { (id, type) ->
        dao.getDownloadStatusAndManagerId(id, type)
            .asLiveData()
            .map { downloadedItem ->
                if (downloadedItem?.downloadStatus == "DOWNLOADING") {
                    // --- THIS IS THE FIX ---
                    // If the database says "DOWNLOADING", we must verify it.
                    val isActuallyDownloading = isDownloadRunning(downloadedItem.downloadManagerId)
                    if (isActuallyDownloading) {
                        "DOWNLOADING" // Status is correct, pass it along.
                    } else {
                        // The database has stale data. The download is not running.
                        Log.w("DownloadFix", "Stale 'DOWNLOADING' record found. Resetting.")
                        // Fix the database in the background.
                        viewModelScope.launch(Dispatchers.IO) {
                            dao.deleteById(downloadedItem.id, downloadedItem.itemType)
                        }
                        null // Return null to show the normal 'Download' button.
                    }
                } else {
                    // For "COMPLETE", "FAILED", or null, we trust the database.
                    downloadedItem?.downloadStatus
                }
            }
    }

    /**
     * Checks with the system's DownloadManager to see if a download is currently active.
     */
    private fun isDownloadRunning(downloadId: Long?): Boolean {
        if (downloadId == null || downloadId == -1L) return false
        try {
            val query = DownloadManager.Query().setFilterById(downloadId)
            val cursor = downloadManager.query(query)
            if (cursor != null && cursor.moveToFirst()) {
                val statusIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
                val status = cursor.getInt(statusIndex)
                cursor.close()
                // Return true only if the download is pending or actively running.
                return status == DownloadManager.STATUS_RUNNING || status == DownloadManager.STATUS_PENDING
            }
        } catch (e: Exception) {
            Log.e("DownloadFix", "Error querying DownloadManager", e)
        }
        return false // If we can't find it, it's not running.
    }

    /**
     * The ONLY public function the Fragment needs to call to start loading everything.
     */
    fun loadItemDetails(id: Int, itemType: String) {
        itemIdAndType.value = id to itemType
    }
}
