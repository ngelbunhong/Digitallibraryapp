package com.library.digitallibrary.utils

import android.app.DownloadManager
import android.content.Context
import android.os.Environment
import android.widget.Toast
import com.library.digitallibrary.data.models.book.Book // Or your item models
import com.library.digitallibrary.data.offline.DownloadedItem
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import androidx.core.net.toUri
import com.library.digitallibrary.data.local.dao.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers

object Downloader {
    @OptIn(DelicateCoroutinesApi::class)
    fun startDownload(context: Context, book: Book) { // Example for a book
        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val uri = book.downloadUrl?.toUri() // Assuming your Book model has a downloadUrl
        val fileName = "${book.id}_${book.title}.pdf"

        val request = DownloadManager.Request(uri)
            .setTitle(book.title)
            .setDescription("Downloading...")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalFilesDir(context, Environment.DIRECTORY_DOWNLOADS, fileName)
            .setAllowedOverMetered(true) // Allow download over mobile data

        val downloadId = downloadManager.enqueue(request)
        Toast.makeText(context, "Download started...", Toast.LENGTH_SHORT).show()

        // Save initial record to Room database
        val downloadedItem = DownloadedItem(
            id = book.id,
            title = book.title,
            author = book.author,
            thumbnailUrl = book.thumbnail,
            localFilePath = null,
            remoteUrl = book.downloadUrl.toString(),
            itemType = "BOOK",
            downloadManagerId = downloadId,
            downloadStatus = "DOWNLOADING"
        )


        // Use a coroutine to insert this record into the database on a background thread
        CoroutineScope(Dispatchers.IO).launch {
            AppDatabase.getDatabase(context).downloadedItemDao().insert(downloadedItem)
        }
    }
}