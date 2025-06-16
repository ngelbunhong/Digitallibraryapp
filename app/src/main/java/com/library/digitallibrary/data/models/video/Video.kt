package com.library.digitallibrary.data.models.video

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Video(
    @SerializedName("id")
    val id: Int,
    @SerializedName("title")
    val title: String,
    @SerializedName("author")
    val author: String,
    @SerializedName("thumbnailUrl")
    val thumbnailUrl: String,
    @SerializedName("duration")
    val duration: String,
    @SerializedName("createdAtTimestamp") // Or "publishedAt", "dateAdded"
    val createdAtTimestamp: Long, // Add this field

    // Add this field to hold the URL for the downloadable video file
    @SerializedName("downloadUrl")
    val downloadUrl: String? = null
): Serializable
