package com.library.digitallibrary.data.models.home

import com.library.digitallibrary.data.models.book.Book
import com.library.digitallibrary.data.models.video.Video

sealed class HomeItem {
    data class BookItem(val book: Book) : HomeItem()
    data class VideoItem(val video: Video) : HomeItem()

    fun getTimestamp(): Long {
        return when (this) {
            is BookItem -> book.createdAtTimestamp
            is VideoItem -> video.createdAtTimestamp
        }
    }
}
