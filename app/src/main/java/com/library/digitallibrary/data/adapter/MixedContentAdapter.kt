package com.library.digitallibrary.data.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.library.digitallibrary.R
import com.library.digitallibrary.data.models.book.Book
import com.library.digitallibrary.data.models.home.HomeItem
import com.library.digitallibrary.data.models.video.Video

class MixedContentAdapter(
    private val items: List<HomeItem>,
    private val listener: ItemClickListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_BOOK = 0
        private const val TYPE_VIDEO = 1
    }

    interface ItemClickListener {
        fun onBookClicked(book: Book)
        fun onVideoClicked(video: Video)
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is HomeItem.BookItem -> TYPE_BOOK
            is HomeItem.VideoItem -> TYPE_VIDEO
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_BOOK -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.vertical_item, parent, false)
                BookViewHolder(view)
            }
            TYPE_VIDEO -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.horizontal_item, parent, false)
                VideoViewHolder(view)
            }
            else -> throw IllegalArgumentException("Unknown view type")
        }
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is HomeItem.BookItem -> {
                (holder as BookViewHolder).bind(item.book)
            }
            is HomeItem.VideoItem -> {
                (holder as VideoViewHolder).bind(item.video)
            }
        }
    }

    inner class BookViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val title = itemView.findViewById<TextView>(R.id.item_title)
        private val author = itemView.findViewById<TextView>(R.id.item_author)
        private val thumbnail = itemView.findViewById<ImageView>(R.id.item_image)

        fun bind(book: Book) {
            title.text = book.title
            author.text = book.author
            Glide.with(itemView.context).load(book.thumbnail).into(thumbnail)
            itemView.setOnClickListener { listener.onBookClicked(book) }
        }
    }

    inner class VideoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val title = itemView.findViewById<TextView>(R.id.itemTitle)
        private val author = itemView.findViewById<TextView>(R.id.itemContent)
        private val thumbnail = itemView.findViewById<ImageView>(R.id.itemImage)

        fun bind(video: Video) {
            title.text = video.title
            author.text = video.author
            Glide.with(itemView.context).load(video.thumbnailUrl).into(thumbnail)
            itemView.setOnClickListener { listener.onVideoClicked(video) }
        }
    }
}
