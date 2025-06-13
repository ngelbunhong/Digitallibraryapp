package com.library.digitallibrary.data.local.dao

import android.content.Context
import androidx.room.*
import com.library.digitallibrary.data.offline.DownloadedItem

@Database(entities = [DownloadedItem::class], version = 2, exportSchema = false) // Use version 2 or higher
abstract class AppDatabase : RoomDatabase() {
    abstract fun downloadedItemDao(): DownloadedItemDao
    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "digital_library_database")
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}