package com.library.digitallibrary.data.offline

import androidx.room.Entity

// Use a composite primary key to uniquely identify an item by its ID AND type.
@Entity(tableName = "downloaded_items", primaryKeys = ["id", "itemType"])
data class DownloadedItem(
    val id: Int,
    val itemType: String, // "BOOK" or "VIDEO"

    val title: String,
    val author: String,
    val thumbnailUrl: String?,
    val localFilePath: String?,
    val remoteUrl: String,
    var downloadManagerId: Long,
    var downloadStatus: String
)