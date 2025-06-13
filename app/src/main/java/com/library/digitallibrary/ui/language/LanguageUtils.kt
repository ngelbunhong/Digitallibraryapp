package com.library.digitallibrary.ui.language // Or your actual package

import android.content.Context
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import java.util.Locale
import androidx.core.content.edit

object LanguageUtils {

    /**
     * Sets the application's language. This is the modern, recommended way.
     * It persists the setting and correctly updates the app's configuration.
     *
     * @param languageCode The ISO 639-1 language code (e.g., "km", "en").
     */
    fun setAppLocale(languageCode: String) {
        val locale = Locale(languageCode)
        // Create a new LocaleList with the desired locale
        val localeList = LocaleListCompat.create(locale)
        // Set the app's locale list
        AppCompatDelegate.setApplicationLocales(localeList)
    }

    /**
     * Retrieves the saved language from SharedPreferences and applies it.
     * If no language is saved, it defaults to Khmer ('km').
     * This should be called once when the app starts.
     *
     * @param context The application context.
     */
    fun applySavedLocale(context: Context) {
        val prefs = context.getSharedPreferences("Settings", Context.MODE_PRIVATE)
        // "km" is correctly set as your primary/default language here
        val language = prefs.getString("app_lang", "km")

        Log.d("LanguageUtils_DEBUG", "Applying locale. Language from storage: '$language'.")

        // No need for 'toString()' if the default value is already a string
        if (language != null) {
            setAppLocale(language.toString())
        }
    }

    /**
     * A helper function for other parts of your app to save a new language choice.
     *
     * @param context The context.
     * @param languageCode The new language code to save and apply.
     */
    fun saveAndApplyLanguage(context: Context, languageCode: String) {
        Log.d("LanguageUtils_DEBUG", "Setting app locale now to: $languageCode")
        val prefs = context.getSharedPreferences("Settings", Context.MODE_PRIVATE)
        prefs.edit { putString("app_lang", languageCode) }

        // Apply the new locale
        setAppLocale(languageCode.toString())
    }
}