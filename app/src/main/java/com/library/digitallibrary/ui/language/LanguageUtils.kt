@file:Suppress("DEPRECATION", "DEPRECATION")

package com.library.digitallibrary.ui.language

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import java.util.Locale

object LanguageUtils {
    @SuppressLint("ObsoleteSdkInt")
    fun setAppLocale(context: Context, languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val config = Configuration()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            config.setLocale(locale)
            context.createConfigurationContext(config)

        } else {
            config.locale = locale
            context.resources.updateConfiguration(config, context.resources.displayMetrics)
        }
    }

    fun applySaveLocale(context: Context) {
        val prefs = context.getSharedPreferences("Settings", Context.MODE_PRIVATE)
        val language = prefs.getString("app_lang", "km")
        setAppLocale(context, language.toString())
    }
}