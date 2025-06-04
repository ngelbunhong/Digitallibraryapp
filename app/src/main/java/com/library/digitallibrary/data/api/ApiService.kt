package com.library.digitallibrary.data.api

import com.library.digitallibrary.data.models.ads.Ads
import com.library.digitallibrary.data.models.book.Book
import com.library.digitallibrary.data.models.video.Video
import retrofit2.http.GET

interface ApiService {

    @GET("books")
    suspend fun getBooks(): List<Book>

    @GET("mock/books")
    suspend fun mockBooks(): List<Book>

    @GET("mock/videos")
    suspend fun mockVideos(): List<Video>


    @GET("ads")
    suspend fun getAds(): List<Ads>
}