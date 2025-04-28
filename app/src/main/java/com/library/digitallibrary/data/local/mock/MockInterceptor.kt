package com.library.digitallibrary.data.local.mock

import android.content.Context
import android.support.annotation.RawRes
import com.library.digitallibrary.R
import okhttp3.Interceptor
import okhttp3.Protocol
import okhttp3.Response

class MockInterceptor(private val context: Context): Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val url = request.url.toString()

        return when {
            url.contains("mock/books") ->{
                val json = readJsonFromRaw(R.raw.mock_book_data)
                Response.Builder()
                    .code(200)
                    .message(json)
                    .request(chain.request())
                    .protocol(Protocol.HTTP_1_1)
                    .build()
            }
            else -> chain.proceed(chain.request())
        }

    }

    private fun readJsonFromRaw(@RawRes resId: Int): String {
        return context.resources.openRawResource(resId).bufferedReader().use { it.readText() }
    }

}