package com.library.digitallibrary.data.retrofit

import android.content.Context
import com.google.gson.GsonBuilder
import com.library.digitallibrary.data.api.ApiService
import com.library.digitallibrary.data.local.mock.MockInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https"
    private const val MOCK_BASE_URL = "http://localhost/"

    fun create(context: Context, useMock: Boolean = false): ApiService {
        val gson = GsonBuilder().create()
        val client = OkHttpClient.Builder()
            .apply {
                if (useMock) {
                    addInterceptor(MockInterceptor(context))
                }
            }
            .build()

        return Retrofit.Builder()
            .baseUrl(if (useMock) MOCK_BASE_URL else BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(ApiService::class.java)
    }

}