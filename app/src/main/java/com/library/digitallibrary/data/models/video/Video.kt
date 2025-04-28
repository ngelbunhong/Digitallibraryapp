package com.library.digitallibrary.data.models.video

import java.io.Serializable

data class Video(
    val id: Int,
    val title: String,
    val thumbnailUrl: String,
    val duration: String
): Serializable
