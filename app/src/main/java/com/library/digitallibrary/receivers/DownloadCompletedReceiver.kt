package com.library.digitallibrary.receivers

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.library.digitallibrary.data.local.dao.AppDatabase
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class DownloadCompletedReceiver : BroadcastReceiver() {
    @OptIn(DelicateCoroutinesApi::class)
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == DownloadManager.ACTION_DOWNLOAD_COMPLETE) {
            val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            if (id != -1L) {
                // Use a coroutine to check status and update the database
                GlobalScope.launch { // In a real app, inject a coroutine scope
                    val dao = AppDatabase.Companion.getDatabase(context).downloadedItemDao()
                    val downloadedItem = dao.getByDownloadManagerId(id)

                    if (downloadedItem != null) {
                        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                        val query = DownloadManager.Query().setFilterById(id)
                        val cursor = downloadManager.query(query)

                        if (cursor.moveToFirst()) {
                            val statusIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
                            val status = cursor.getInt(statusIndex)

                            if (status == DownloadManager.STATUS_SUCCESSFUL) {
                                val uriIndex = cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI)
                                val uriString = cursor.getString(uriIndex)
                                dao.updateStatus(id, "COMPLETE", uriString)
                            } else {
                                dao.updateStatus(id, "FAILED", null)
                            }
                        }
                        cursor.close()
                    }
                }
            }
        }
    }
}