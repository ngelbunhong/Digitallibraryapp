package com.library.digitallibrary.data.models.ads

data class Ads(
    val id: Int,
    val imageUrl: String? = null, // For remote
    val imageResId: Int? = null,  // For local drawable
    val title: String
)
