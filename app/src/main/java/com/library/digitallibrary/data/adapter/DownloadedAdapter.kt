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
import com.library.digitallibrary.data.offline.DownloadedItem

class DownloadedAdapter(
    private val listener: (DownloadedItem) -> Unit
) : ListAdapter<DownloadedItem, DownloadedAdapter.DownloadedViewHolder>(DownloadedDiffCallback()) {

    inner class DownloadedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val thumbnail: ImageView = itemView.findViewById(R.id.item_image)
        private val typeIcon: ImageView = itemView.findViewById(R.id.item_type_icon)
        private val title: TextView = itemView.findViewById(R.id.item_title)
        private val author: TextView = itemView.findViewById(R.id.item_author)

        fun bind(item: DownloadedItem) {
            title.text = item.title
            author.text = item.author
            Glide.with(itemView.context)
                .load(item.thumbnailUrl?.ifEmpty { R.drawable.placeholder_image })
                .placeholder(R.drawable.placeholder_image)
                .into(thumbnail)
            typeIcon.setImageResource(if (item.itemType == "BOOK") R.drawable.ic_book else R.drawable.ic_card_video)
            itemView.setOnClickListener { listener(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DownloadedViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_downloaded, parent, false)
        return DownloadedViewHolder(view)
    }

    override fun onBindViewHolder(holder: DownloadedViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class DownloadedDiffCallback : DiffUtil.ItemCallback<DownloadedItem>() {
    override fun areItemsTheSame(oldItem: DownloadedItem, newItem: DownloadedItem): Boolean {
        return oldItem.id == newItem.id && oldItem.itemType == newItem.itemType
    }
    override fun areContentsTheSame(oldItem: DownloadedItem, newItem: DownloadedItem): Boolean {
        return oldItem == newItem
    }
}