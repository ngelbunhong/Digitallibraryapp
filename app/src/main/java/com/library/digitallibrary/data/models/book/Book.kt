package com.library.digitallibrary.data.models.book

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Book(
    @SerializedName("id")
    val id: Int,
    val title: String,
    val author: String,
    val thumbnail: String,
    val year: Int,
    val isAvailable: Boolean,
    val tags: List<String>
): Serializable