package com.library.digitallibrary.data.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.library.digitallibrary.R
import com.library.digitallibrary.data.models.book.Book

class BookAdapter(
    private val listener: (Book) -> Unit
) : ListAdapter<Book, BookAdapter.BookViewHolder>(BookDiffCallback()) {

    inner class BookViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val title = itemView.findViewById<TextView>(R.id.item_title)
        private val author = itemView.findViewById<TextView>(R.id.item_author)
        private val thumbnail = itemView.findViewById<ImageView>(R.id.item_image)

        fun bind(book: Book) {
            title.text = book.title
            author.text = book.author
            Glide.with(itemView.context).load(book.thumbnail).into(thumbnail)
            itemView.setOnClickListener { listener(book) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.vertical_item, parent, false)

        val screenWidth = parent.resources.displayMetrics.widthPixels
        val itemWidth = screenWidth / 4
        view.layoutParams = ViewGroup.LayoutParams(itemWidth, ViewGroup.LayoutParams.WRAP_CONTENT)
        return BookViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class BookDiffCallback : DiffUtil.ItemCallback<Book>() {
        override fun areItemsTheSame(oldItem: Book, newItem: Book): Boolean {
            return oldItem.id == newItem.id // Adjust based on your model's unique identifier
        }

        override fun areContentsTheSame(oldItem: Book, newItem: Book): Boolean {
            return oldItem == newItem
        }
    }
}
