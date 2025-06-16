package com.library.digitallibrary.utils

import com.library.digitallibrary.data.local.dao.AppDatabase
import com.library.digitallibrary.data.offline.DownloadedItem
import android.app.DownloadManager
import android.content.Context
import android.os.Environment
import android.widget.Toast
import com.library.digitallibrary.data.models.book.Book
import com.library.digitallibrary.data.models.video.Video
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.core.net.toUri

object Downloader {

    fun startDownload(context: Context, book: Book) {
        val remoteUrl = book.downloadUrl ?: ""
        if (remoteUrl.isBlank()) {
            Toast.makeText(context, "No download link available.", Toast.LENGTH_SHORT).show()
            return
        }
        val fileName = "${book.id}_${book.title}.pdf"
        enqueueDownload(context, remoteUrl, book.title, fileName, "BOOK", book.id, book.author, book.thumbnail)
    }

    // NEW function for videos
    fun startDownload(context: Context, video: Video) {
        val remoteUrl = video.downloadUrl ?: ""
        if (remoteUrl.isBlank()) {
            Toast.makeText(context, "No download link available.", Toast.LENGTH_SHORT).show()
            return
        }
        val fileName = "${video.id}_${video.title}.mp4"
        enqueueDownload(context, remoteUrl, video.title, fileName, "VIDEO", video.id, video.author, video.thumbnailUrl)
    }

    private fun enqueueDownload(
        context: Context,
        remoteUrl: String,
        title: String,
        fileName: String,
        itemType: String,
        itemId: Int,
        author: String,
        thumbnailUrl: String?
    ) {
        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val request = DownloadManager.Request(remoteUrl.toUri())
            .setTitle(title)
            .setDescription("Downloading...")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalFilesDir(context, Environment.DIRECTORY_DOWNLOADS, fileName)

        val downloadId = downloadManager.enqueue(request)
        Toast.makeText(context, "Download started...", Toast.LENGTH_SHORT).show()

        val downloadedItem = DownloadedItem(
            id = itemId, itemType = itemType, title = title, author = author,
            thumbnailUrl = thumbnailUrl, localFilePath = null, remoteUrl = remoteUrl,
            downloadManagerId = downloadId, downloadStatus = "DOWNLOADING"
        )

        CoroutineScope(Dispatchers.IO).launch {
            AppDatabase.getDatabase(context).downloadedItemDao().insert(downloadedItem)
        }
    }
}