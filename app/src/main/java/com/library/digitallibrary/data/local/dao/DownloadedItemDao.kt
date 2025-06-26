package com.library.digitallibrary.data.local.dao

import androidx.room.*
import com.library.digitallibrary.data.offline.DownloadedItem
import kotlinx.coroutines.flow.Flow

@Dao
interface DownloadedItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: DownloadedItem)

    @Delete
    suspend fun delete(item: DownloadedItem)

    /**
     * Deletes an item from the database using its composite primary key.
     * This is used by the ViewModel to clean up stale download entries.
     */
    @Query("DELETE FROM downloaded_items WHERE id = :itemId AND itemType = :itemType")
    suspend fun deleteById(itemId: Int, itemType: String)

    @Query("SELECT * FROM downloaded_items WHERE downloadStatus = 'COMPLETE' AND title LIKE '%' || :searchQuery || '%' ORDER BY title ASC")
    fun getCompletedDownloads(searchQuery: String): Flow<List<DownloadedItem>>

    @Query("SELECT * FROM downloaded_items WHERE downloadManagerId = :downloadId")
    suspend fun getByDownloadManagerId(downloadId: Long): DownloadedItem?

    @Query("UPDATE downloaded_items SET downloadStatus = :status, localFilePath = :filePath WHERE downloadManagerId = :downloadId")
    suspend fun updateStatus(downloadId: Long, status: String, filePath: String?)

    /**
     * Gets the full DownloadedItem object for a given item ID and type.
     * This replaces the old `getDownloadStatus` function, as it provides the `downloadManagerId`
     * which is needed by the ViewModel to verify the download status.
     */
    @Query("SELECT * FROM downloaded_items WHERE id = :itemId AND itemType = :itemType")
    fun getDownloadStatusAndManagerId(itemId: Int, itemType: String): Flow<DownloadedItem?>

}
