package com.library.digitallibrary.data.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter // Import ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.library.digitallibrary.R
import com.library.digitallibrary.data.models.book.Book
import com.library.digitallibrary.data.models.home.HomeItem
import com.library.digitallibrary.data.models.video.Video

class MixedContentAdapter(
    private val listener: ItemClickListener
) : ListAdapter<HomeItem, MixedContentAdapter.UnifiedViewHolder>(HomeItemDiffCallback()) {

    // The listener interface remains the same
    interface ItemClickListener {
        fun onBookClicked(book: Book)
        fun onVideoClicked(video: Video)
    }

    // There is only ONE ViewHolder type now
    inner class UnifiedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val thumbnail: ImageView = itemView.findViewById(R.id.item_image)
        private val title: TextView = itemView.findViewById(R.id.item_title)
        private val author: TextView = itemView.findViewById(R.id.item_author)

        fun bindBook(book: Book) {
            title.text = book.title
            author.text = book.author

            Glide.with(itemView.context)
                .load(book.thumbnail)
                .into(thumbnail)

            itemView.setOnClickListener { listener.onBookClicked(book) }
        }

        fun bindVideo(video: Video) {
            title.text = video.title
            author.text = video.author

            Glide.with(itemView.context)
                .load(video.thumbnailUrl)
                .into(thumbnail)

            itemView.setOnClickListener { listener.onVideoClicked(video) }
        }
    }

    // getItemViewType is no longer needed because there's only one type of layout.

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UnifiedViewHolder {
        // Always inflate the single, unified layout
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.book_item, parent, false)
        return UnifiedViewHolder(view)
    }

    override fun onBindViewHolder(holder: UnifiedViewHolder, position: Int) {
        val currentItem = getItem(position)
        if (currentItem != null) {
            when (currentItem) {
                is HomeItem.BookItem -> holder.bindBook(currentItem.book)
                is HomeItem.VideoItem -> holder.bindVideo(currentItem.video)
            }
        }
    }
}

class HomeItemDiffCallback : DiffUtil.ItemCallback<HomeItem>() {
    override fun areItemsTheSame(oldItem: HomeItem, newItem: HomeItem): Boolean {
        // Check if items represent the same database entity or unique object.
        // This usually involves checking IDs.
        return when {
            oldItem is HomeItem.BookItem && newItem is HomeItem.BookItem ->
                oldItem.book.id == newItem.book.id

            oldItem is HomeItem.VideoItem && newItem is HomeItem.VideoItem ->
                oldItem.video.id == newItem.video.id

            else -> false // Different types or one is null (though ListAdapter usually handles non-null lists)
        }
    }

    override fun areContentsTheSame(oldItem: HomeItem, newItem: HomeItem): Boolean {
        // Check if the visible data of the items is the same.
        // Relies on the data class's generated equals() method.
        return oldItem == newItem
    }
}