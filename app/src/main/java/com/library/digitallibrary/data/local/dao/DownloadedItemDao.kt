package com.library.digitallibrary.data.local.dao


import androidx.room.*
import com.library.digitallibrary.data.offline.DownloadedItem
import kotlinx.coroutines.flow.Flow

@Dao
interface DownloadedItemDao {
    // Using @Insert is simpler for the restore/undo logic
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: DownloadedItem)

    // Using @Delete is simpler for the delete logic
    @Delete
    suspend fun delete(item: DownloadedItem)

    @Query("SELECT * FROM downloaded_items WHERE downloadStatus = 'COMPLETE' AND title LIKE '%' || :searchQuery || '%' ORDER BY title ASC")
    fun getCompletedDownloads(searchQuery: String): Flow<List<DownloadedItem>>

    @Query("SELECT * FROM downloaded_items WHERE downloadManagerId = :downloadId")
    suspend fun getByDownloadManagerId(downloadId: Long): DownloadedItem?

    @Query("UPDATE downloaded_items SET downloadStatus = :status, localFilePath = :filePath WHERE downloadManagerId = :downloadId")
    suspend fun updateStatus(downloadId: Long, status: String, filePath: String?)
}