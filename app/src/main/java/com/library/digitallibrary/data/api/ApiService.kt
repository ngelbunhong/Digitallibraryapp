package com.library.digitallibrary.data.api

import com.library.digitallibrary.data.models.book.Book
import retrofit2.Call
import retrofit2.http.GET

interface ApiService {

    @GET("books")
    fun getBooks(): List<Book>

    @GET("mock/books")
    fun mockBooks(): List<Book>

}