package com.library.digitallibrary.data.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.library.digitallibrary.R
import com.library.digitallibrary.data.models.video.Video

class VideoAdapter(
    private val videos: List<Video>,
    private val listener: (Video) -> Unit
) : RecyclerView.Adapter<VideoAdapter.VideoViewHolder>() {

    inner class VideoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val title: TextView = itemView.findViewById(R.id.itemTitle)
        private val author: TextView = itemView.findViewById(R.id.itemContent)
        private val thumbnail = itemView.findViewById<ImageView>(R.id.itemImage)

        fun bind(video: Video) {
            title.text = video.title
            author.text = video.author
            Glide.with(itemView.context).load(video.thumbnailUrl).into(thumbnail)
            itemView.setOnClickListener { listener(video) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.horizontal_item, parent, false)
        return VideoViewHolder(view)
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        holder.bind(videos[position])
    }

    override fun getItemCount() = videos.size
}
