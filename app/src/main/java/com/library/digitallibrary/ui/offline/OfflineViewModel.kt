package com.library.digitallibrary.ui.offline

import android.app.Application
import androidx.lifecycle.*
import com.library.digitallibrary.data.local.dao.AppDatabase
import com.library.digitallibrary.data.offline.DownloadedItem
import kotlinx.coroutines.launch

class OfflineViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = AppDatabase.getDatabase(application).downloadedItemDao()
    private val searchQuery = MutableLiveData("")

    // This reactively queries the database whenever the search query changes.
    val downloads: LiveData<List<DownloadedItem>> = searchQuery.switchMap { query ->
        dao.getCompletedDownloads(query).asLiveData()
    }

    private var recentlyDeletedItem: DownloadedItem? = null


    fun setSearchQuery(query: String) {
        searchQuery.value = query
    }

    fun deleteItem(item: DownloadedItem) {
        viewModelScope.launch { dao.delete(item) }
    }

    fun restoreItem(item: DownloadedItem) {
        viewModelScope.launch { dao.insert(item) }
    }
}