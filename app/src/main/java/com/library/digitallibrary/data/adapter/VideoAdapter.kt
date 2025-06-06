package com.library.digitallibrary.data.adapter // Or your preferred package

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
import com.library.digitallibrary.data.models.video.Video // Ensure this import is correct

class VideoAdapter(
    private val listener: (Video) -> Unit // Listener for item clicks
) : ListAdapter<Video, VideoAdapter.VideoViewHolder>(VideoDiffCallback()) { // Extend ListAdapter

    inner class VideoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val title: TextView = itemView.findViewById(R.id.itemTitle)
        private val author: TextView = itemView.findViewById(R.id.itemContent) // Assuming these IDs are in horizontal_item.xml
        private val thumbnail: ImageView = itemView.findViewById(R.id.itemImage)

        fun bind(video: Video) {
            title.text = video.title
            author.text = video.author // Or any other relevant field from your Video model
            Glide.with(itemView.context).load(video.thumbnailUrl).into(thumbnail)
            itemView.setOnClickListener { listener(video) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.video_item, parent, false) // Using horizontal_item.xml
        return VideoViewHolder(view)
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        // Use getItem(position) to get the Video object for the current position
        val video = getItem(position)
        // ListAdapter ensures getItem(position) is not null if used correctly.
        // You might add a null check if there's any extreme edge case,
        // but generally, it's safe to assume non-null with ListAdapter's typical usage.
        if (video != null) {
            holder.bind(video)
        }
    }

    class VideoDiffCallback : DiffUtil.ItemCallback<Video>() {
        override fun areItemsTheSame(oldItem: Video, newItem: Video): Boolean {
            return oldItem.id == newItem.id // Adjust based on your model's unique identifier
        }

        override fun areContentsTheSame(oldItem: Video, newItem: Video): Boolean {
            return oldItem == newItem
        }
    }
}