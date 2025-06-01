package com.library.digitallibrary.data.models.ads

import android.support.annotation.StringRes

data class Ads(
    val id: Int,
    val imageUrl: String? = null, // For remote
    val imageResId: Int? = null,  // For local drawable
    val title: String? = null,
    @StringRes val titleResId: Int? = null  // Store the string resource ID
)
