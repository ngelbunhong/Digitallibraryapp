package com.library.digitallibrary.data.models.home

import androidx.annotation.StringRes
import com.library.digitallibrary.data.models.ads.Ads
import com.library.digitallibrary.data.models.book.Book
import com.library.digitallibrary.data.models.video.Video

sealed class HomeScreenItem(val id: String) {
    data class AdsSection(val ads: List<Ads>) : HomeScreenItem("ads_section")
    data class CardItemSection(val cards: List<Ads>) : HomeScreenItem("card_item_section")

    // --- CHANGED ---
    // Now takes a String Resource ID instead of a raw String.
    // The @StringRes annotation helps Android Studio check that you are passing a valid R.string.
    data class TitledBookSection(@StringRes val titleResId: Int, val books: List<Book>) : HomeScreenItem("books_$titleResId")
    data class TitledVideoSection(@StringRes val titleResId: Int, val videos: List<Video>) : HomeScreenItem("videos_$titleResId")
    data class TitledMixedSection(@StringRes val titleResId: Int, val items: List<HomeItem>) : HomeScreenItem("mixed_$titleResId")

    object Copyright : HomeScreenItem("copyright")
}