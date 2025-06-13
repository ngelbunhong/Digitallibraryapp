package com.library.digitallibrary.receivers

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.library.digitallibrary.data.local.dao.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DownloadCompletedReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != DownloadManager.ACTION_DOWNLOAD_COMPLETE) {
            return
        }

        // 1. Tell the system we are starting background work.
        val pendingResult: PendingResult = goAsync()

        val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
        if (id == -1L) {
            Log.e("Download_DEBUG", "Receiver started but the download ID was invalid.")
            pendingResult.finish() // Always finish the pending result
            return
        }

        Log.d("Download_DEBUG", "--- RECEIVER STARTED for download ID: $id ---")

        CoroutineScope(Dispatchers.IO).launch {
            try {
                // All your existing database logic is correct
                val dao = AppDatabase.getDatabase(context).downloadedItemDao()
                val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                val query = DownloadManager.Query().setFilterById(id)
                val cursor = downloadManager.query(query)

                if (cursor != null && cursor.moveToFirst()) {
                    val statusIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
                    val status = cursor.getInt(statusIndex)

                    if (status == DownloadManager.STATUS_SUCCESSFUL) {
                        val uriIndex = cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI)
                        val fileUri = cursor.getString(uriIndex)
                        Log.d("Download_DEBUG", "SUCCESS! Updating status to COMPLETE.")
                        dao.updateStatus(id, "COMPLETE", fileUri)
                    } else {
                        Log.e("Download_DEBUG", "Download failed with status: $status. Updating DB.")
                        dao.updateStatus(id, "FAILED", null)
                    }
                    cursor.close()
                } else {
                    Log.e("Download_DEBUG", "ERROR: Could not find download in manager for ID $id.")
                }
            } catch (e: Exception) {
                Log.e("Download_DEBUG", "CRASH: An error occurred while updating the database.", e)
            } finally {
                // 2. CRUCIAL: Tell the system we have finished our background work.
                Log.d("Download_DEBUG", "--- Receiver finished its work. ---")
                pendingResult.finish()
            }
        }
    }
}