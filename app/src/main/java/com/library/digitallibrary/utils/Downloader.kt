package com.library.digitallibrary.utils

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.widget.Toast
import com.library.digitallibrary.data.local.dao.AppDatabase
import com.library.digitallibrary.data.models.book.Book
import com.library.digitallibrary.data.offline.DownloadedItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.core.net.toUri

object Downloader {
    private const val TAG = "Downloader"

    fun startDownload(context: Context, book: Book) {
        val remoteUrl = book.downloadUrl ?: run {
            showToast(context, "No download link available")
            return
        }

        try {
            val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            val fileName = "${book.id}_${sanitizeFileName(book.title)}.pdf"

            val request = DownloadManager.Request(remoteUrl.toUri()).apply {
                setTitle("Downloading: ${book.title}")
                setDescription("Please wait...")
                setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
                setAllowedOverMetered(true)
                setAllowedOverRoaming(true)
            }

            val downloadId = downloadManager.enqueue(request)
            showToast(context, "Download started")

            val downloadedItem = DownloadedItem(
                id = book.id,
                itemType = "BOOK",
                title = book.title,
                author = book.author,
                thumbnailUrl = book.thumbnail,
                localFilePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                    .resolve(fileName).absolutePath,
                remoteUrl = remoteUrl,
                downloadManagerId = downloadId,
                downloadStatus = "DOWNLOADING"
            )

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    AppDatabase.getDatabase(context).downloadedItemDao().insert(downloadedItem)
                    Log.i(TAG, "Inserted download record for ${book.title} (ID: $downloadId)")
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to insert download record", e)
                    // Cancel download if DB insert fails
                    downloadManager.remove(downloadId)
                    withContext(Dispatchers.Main) {
                        showToast(context, "Failed to start download")
                    }
                }
            }
        } catch (e: SecurityException) {
            Log.e(TAG, "Storage permission denied", e)
            showToast(context, "Please grant storage permission")
        } catch (e: Exception) {
            Log.e(TAG, "Download failed to start", e)
            showToast(context, "Download failed to start")
        }
    }

    private fun sanitizeFileName(title: String): String {
        return title.replace("[^a-zA-Z0-9-.]".toRegex(), "_")
    }

    private fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}