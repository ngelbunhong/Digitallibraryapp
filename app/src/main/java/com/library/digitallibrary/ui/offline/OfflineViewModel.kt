package com.library.digitallibrary.ui.offline


import android.app.Application
import androidx.lifecycle.*
import com.library.digitallibrary.data.local.dao.AppDatabase
import com.library.digitallibrary.data.offline.DownloadedItem
import kotlinx.coroutines.launch

class OfflineViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = AppDatabase.getDatabase(application).downloadedItemDao()
    private val searchQuery = MutableLiveData("")

    // This is a reactive stream. Whenever searchQuery changes, it re-queries the database.
    val downloads: LiveData<List<DownloadedItem>> = searchQuery.switchMap { query ->
        dao.getCompletedDownloads(query).asLiveData()
    }

    // For Undo
    private var recentlyDeletedItem: DownloadedItem? = null

    fun setSearchQuery(query: String) {
        searchQuery.value = query
    }

    fun deleteItem(item: DownloadedItem) {
        recentlyDeletedItem = item
        viewModelScope.launch {
            dao.delete(item)
            // You would also delete the actual file from storage here.
        }
    }

    fun restoreItem() {
        recentlyDeletedItem?.let {
            viewModelScope.launch {
                dao.insert(it)
            }
        }
    }
}