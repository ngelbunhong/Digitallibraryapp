package com.library.digitallibrary

import android.app.Application
import android.util.Log
import com.library.digitallibrary.ui.language.LanguageUtils

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        Log.d("MyApplication_DEBUG", "Application onCreate is running. Applying saved locale...")
        LanguageUtils.applySavedLocale(this)
    }
}