package com.library.digitallibrary.data.local.mock

import android.content.Context
import android.support.annotation.RawRes
import com.library.digitallibrary.R
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody

class MockInterceptor(private val context: Context) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val url = request.url.toString()

        val (code, body) = when {
            url.contains("mock/books") -> 200 to readJsonFromRaw(R.raw.mock_book_data)
            url.contains("mock/videos") -> 200 to readJsonFromRaw(R.raw.mock_video_data)
//            url.contains("mock/ads") -> 200 to readJsonFromRaw(R.raw.mo)
            else -> return chain.proceed(chain.request())
        }

        return Response.Builder()
            .code(code)
            .message(body)
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .body(body.toResponseBody("application/json".toMediaTypeOrNull()))
            .addHeader("content-type", "application/json")
            .build()
    }

    private fun readJsonFromRaw(@RawRes resId: Int): String {
        return context.resources.openRawResource(resId)
            .bufferedReader()
            .use { it.readText() }
    }
}
