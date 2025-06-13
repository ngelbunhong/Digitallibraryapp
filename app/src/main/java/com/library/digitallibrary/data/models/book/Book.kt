package com.library.digitallibrary.data.models.book

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Book(
    @SerializedName("id")
    val id: Int,
    @SerializedName("title")
    val title: String,
    @SerializedName("author")
    val author: String,
    @SerializedName("thumbnail")
    val thumbnail: String,
    val downloadUrl: String? = null,
    val year: Int, // Keep if you still need it for other purposes
    val isAvailable: Boolean,
    val tags: List<String>,
    @SerializedName("createdAtTimestamp") // Or "publishedAt", "dateAdded"
    val createdAtTimestamp: Long // Add this field
): Serializable