package com.library.digitallibrary.data.adapter

import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.library.digitallibrary.R
import com.library.digitallibrary.data.adapter.HomeAdapter.HomeAdapterListener
import com.library.digitallibrary.data.models.ads.Ads
import com.library.digitallibrary.data.models.book.Book
import com.library.digitallibrary.data.models.home.HomeScreenItem
import com.library.digitallibrary.data.models.video.Video

// --- View Type constants ---
private const val TYPE_ADS = 1
private const val TYPE_CARDS = 2
private const val TYPE_TITLED_SECTION = 3
private const val TYPE_COPYRIGHT = 4

class HomeAdapter(private val listener: HomeAdapterListener) :
    ListAdapter<HomeScreenItem, RecyclerView.ViewHolder>(HomeScreenItemDiffCallback()) {

    interface HomeAdapterListener {
        fun onSeeMoreClicked(item: HomeScreenItem)
        fun onBookItemClicked(book: Book)
        fun onVideoItemClicked(video: Video)
        fun onAdClicked(ad: Ads)
        fun onCardItemClicked(cardItem: Ads)
    }

    // --- FIX: The shared RecycledViewPool has been removed to prevent ClassCastExceptions. ---

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is HomeScreenItem.AdsSection -> TYPE_ADS
            is HomeScreenItem.CardItemSection -> TYPE_CARDS
            is HomeScreenItem.TitledBookSection,
            is HomeScreenItem.TitledVideoSection,
            is HomeScreenItem.TitledMixedSection -> TYPE_TITLED_SECTION
            is HomeScreenItem.Copyright -> TYPE_COPYRIGHT
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_ADS -> AdsViewHolder(
                inflater.inflate(R.layout.item_home_viewpager, parent, false),
                listener
            )
            TYPE_CARDS -> NestedRecyclerViewHolder(
                inflater.inflate(R.layout.item_home_nested_recycler, parent, false),
                listener // Pass listener only
            )
            TYPE_TITLED_SECTION -> TitledSectionViewHolder(
                inflater.inflate(R.layout.item_home_card_section, parent, false),
                listener // Pass listener only
            )
            TYPE_COPYRIGHT -> CopyrightViewHolder(
                inflater.inflate(R.layout.item_copyright, parent, false)
            )
            else -> throw IllegalArgumentException("Unknown view type: $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is HomeScreenItem.AdsSection -> (holder as AdsViewHolder).bind(item)
            is HomeScreenItem.CardItemSection -> (holder as NestedRecyclerViewHolder).bindCardItems(item)
            is HomeScreenItem.TitledBookSection -> (holder as TitledSectionViewHolder).bindBooks(item)
            is HomeScreenItem.TitledVideoSection -> (holder as TitledSectionViewHolder).bindVideos(item)
            is HomeScreenItem.TitledMixedSection -> (holder as TitledSectionViewHolder).bindMixedGrid(item)
            is HomeScreenItem.Copyright -> { /* No data to bind */ }
        }
    }

    class TitledSectionViewHolder(
        view: View,
        private val listener: HomeAdapterListener
    ) : RecyclerView.ViewHolder(view) {
        private val sectionTitle: TextView = view.findViewById(R.id.section_title)
        private val seeMoreButton: ImageButton = view.findViewById(R.id.see_more_button)
        private val innerRecyclerView: RecyclerView = view.findViewById(R.id.inner_recycler_view)

        fun bindBooks(item: HomeScreenItem.TitledBookSection) {
            val titleText = itemView.context.getString(item.titleResId)
            sectionTitle.text = titleText
            seeMoreButton.setOnClickListener { listener.onSeeMoreClicked(item) }

            val bookAdapter = BookAdapter { book -> listener.onBookItemClicked(book) }
            innerRecyclerView.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                adapter = bookAdapter
                // --- FIX: The setRecycledViewPool call is removed. ---
            }
            bookAdapter.submitList(item.books)
        }

        fun bindVideos(item: HomeScreenItem.TitledVideoSection) {
            val titleText = itemView.context.getString(item.titleResId)
            sectionTitle.text = titleText
            seeMoreButton.setOnClickListener { listener.onSeeMoreClicked(item) }

            val videoAdapter = VideoAdapter { video -> listener.onVideoItemClicked(video) }
            val spanCount = if ((itemView.context.resources.configuration.smallestScreenWidthDp >= 600)) 2 else 2
            innerRecyclerView.apply {
                layoutManager = GridLayoutManager(context, spanCount)
                adapter = videoAdapter
                // --- FIX: The setRecycledViewPool call is removed. ---
            }
            videoAdapter.submitList(item.videos)
        }

        fun bindMixedGrid(item: HomeScreenItem.TitledMixedSection) {
            val titleText = itemView.context.getString(item.titleResId)
            sectionTitle.text = titleText
            seeMoreButton.setOnClickListener { listener.onSeeMoreClicked(item) }

            val mixedAdapter = MixedContentAdapter(object : MixedContentAdapter.ItemClickListener {
                override fun onBookClicked(book: Book) { listener.onBookItemClicked(book) }
                override fun onVideoClicked(video: Video) { listener.onVideoItemClicked(video) }
            })
            innerRecyclerView.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                adapter = mixedAdapter
                // --- FIX: The setRecycledViewPool call is removed. ---
            }
            mixedAdapter.submitList(item.items)
        }
    }
}


class AdsViewHolder(view: View, listener: HomeAdapterListener) : RecyclerView.ViewHolder(view) {
    private val viewPager: ViewPager2 = view.findViewById(R.id.viewPagerAds)
    private val indicatorLayout: LinearLayout = view.findViewById(R.id.indicatorLayout)
    private val adsAdapter = AdsAdapter { ad -> listener.onAdClicked(ad) }
    private val autoScrollHelper = Handler(Looper.getMainLooper())
    private var currentPage = 0
    private var scrollerRunnable: Runnable

    init {
        viewPager.adapter = adsAdapter
        scrollerRunnable = Runnable {
            val itemCount = adsAdapter.itemCount
            if (itemCount == 0) return@Runnable
            currentPage = (currentPage + 1) % itemCount
            viewPager.setCurrentItem(currentPage, true)
            autoScrollHelper.postDelayed(scrollerRunnable, 5000)
        }

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                currentPage = position
                updateIndicators(position)
            }
        })

        itemView.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(v: View) { autoScrollHelper.postDelayed(scrollerRunnable, 5000) }
            override fun onViewDetachedFromWindow(v: View) { autoScrollHelper.removeCallbacks(scrollerRunnable) }
        })
    }

    fun bind(item: HomeScreenItem.AdsSection) {
        adsAdapter.submitList(item.ads)
        createIndicators(item.ads.size)
        autoScrollHelper.removeCallbacks(scrollerRunnable)
        autoScrollHelper.postDelayed(scrollerRunnable, 5000)
    }

    private fun createIndicators(count: Int) {
        indicatorLayout.removeAllViews()
        if (count <= 1) return
        for (i in 0 until count) {
            indicatorLayout.addView(ImageView(itemView.context).apply {
                setImageResource(R.drawable.circle_selector)
                layoutParams = LinearLayout.LayoutParams(24, 24).apply { marginStart = 8; marginEnd = 8 }
            })
        }
        updateIndicators(viewPager.currentItem)
    }

    private fun updateIndicators(selectedIndex: Int) {
        for (i in 0 until indicatorLayout.childCount) {
            (indicatorLayout.getChildAt(i) as ImageView).isSelected = (i == selectedIndex)
        }
    }
}


class NestedRecyclerViewHolder(
    view: View,
    private val listener: HomeAdapterListener
) : RecyclerView.ViewHolder(view) {
    private val recyclerView: RecyclerView = view.findViewById(R.id.inner_recycler_view)
    fun bindCardItems(item: HomeScreenItem.CardItemSection) {
        val cardAdapter = CardItemAdapter { cardItem -> listener.onCardItemClicked(cardItem) }
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = cardAdapter
            // --- FIX: The setRecycledViewPool call is removed. ---
        }
        cardAdapter.submitList(item.cards)
    }
}

class CopyrightViewHolder(view: View) : RecyclerView.ViewHolder(view)

class HomeScreenItemDiffCallback : DiffUtil.ItemCallback<HomeScreenItem>() {
    override fun areItemsTheSame(old: HomeScreenItem, new: HomeScreenItem): Boolean = old.id == new.id
    override fun areContentsTheSame(old: HomeScreenItem, new: HomeScreenItem): Boolean = old == new
}
