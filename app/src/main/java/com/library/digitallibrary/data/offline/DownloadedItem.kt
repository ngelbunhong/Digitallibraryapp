package com.library.digitallibrary.data.offline

import androidx.room.Entity

@Entity(tableName = "downloaded_items", primaryKeys = ["id", "itemType"])
data class DownloadedItem(
    val id: Int,
    val itemType: String, // "BOOK" or "VIDEO"
    val title: String,
    val author: String,
    val thumbnailUrl: String?,
    val localFilePath: String?, // Path to the file on the device
    val remoteUrl: String,      // The URL we download from
    var downloadManagerId: Long,
    var downloadStatus: String  // e.g., "DOWNLOADING", "COMPLETE"
)