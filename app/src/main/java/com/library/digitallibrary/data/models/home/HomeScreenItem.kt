package com.library.digitallibrary.data.models.home

import androidx.annotation.StringRes
import com.library.digitallibrary.data.models.ads.Ads
import com.library.digitallibrary.data.models.book.Book
import com.library.digitallibrary.data.models.video.Video

/**
 * A sealed class representing all possible types of items that can be displayed on the home screen.
 * Each item has a stable `id` to help the RecyclerView's DiffUtil perform efficiently.
 */
sealed class HomeScreenItem {
    // Each item must have a unique and stable ID.
    abstract val id: String

    data class AdsSection(val ads: List<Ads>) : HomeScreenItem() {
        // A hardcoded, stable ID for this unique section.
        override val id: String = "ads_section"
    }

    data class CardItemSection(val cards: List<Ads>) : HomeScreenItem() {
        override val id: String = "card_item_section"
    }

    /**
     * Represents a section with a title that displays a list of books.
     * @param titleResId The string resource for the section's title.
     * @param books The list of books to display.
     * @param categoryId A unique, stable identifier for this specific category (e.g., "featured_books").
     * This is used for navigation when "See More" is clicked.
     */
    data class TitledBookSection(
        @StringRes val titleResId: Int,
        val books: List<Book>,
        val categoryId: String
    ) : HomeScreenItem() {
        // The ID is derived from the categoryId to ensure it's unique per section.
        override val id: String = "books_$categoryId"
    }

    /**
     * Represents a section with a title that displays a list of videos.
     * @param categoryId A unique identifier for this category (e.g., "trending_videos").
     */
    data class TitledVideoSection(
        @StringRes val titleResId: Int,
        val videos: List<Video>,
        val categoryId: String
    ) : HomeScreenItem() {
        override val id: String = "videos_$categoryId"
    }

    /**
     * Represents a section with a title that displays a mixed list of content.
     * @param categoryId A unique identifier for this category (e.g., "new_releases").
     */
    data class TitledMixedSection(
        @StringRes val titleResId: Int,
        val items: List<HomeItem>,
        val categoryId: String
    ) : HomeScreenItem() {
        override val id: String = "mixed_$categoryId"
    }

    object Copyright : HomeScreenItem() {
        override val id: String = "copyright"
    }
}
