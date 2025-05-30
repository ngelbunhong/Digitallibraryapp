package com.library.digitallibrary.data.local.repository

import com.library.digitallibrary.data.api.ApiService
import com.library.digitallibrary.data.models.ads.Ads

class AdRepository(private val apiService: ApiService) {
    suspend fun fetchAds(): List<Ads> {
        return apiService.getAds()
    }
}