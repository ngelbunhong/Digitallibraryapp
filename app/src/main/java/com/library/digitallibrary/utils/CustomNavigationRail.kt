package com.library.digitallibrary.utils

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import com.library.digitallibrary.R

/*
 * Step 1: CustomNavigationRail.kt
 * Create a custom vertical navigation rail using a LinearLayout with manual selection and styling
 */

class CustomNavigationRail @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val items = mutableListOf<View>()
    private var selectedIndex = -1
    private var onItemSelected: ((Int) -> Unit)? = null

    init {
        orientation = VERTICAL
        gravity = Gravity.TOP
        setPadding(0, dpToPx(16), 0, dpToPx(16))
    }

    fun setItems(itemList: List<Pair<Int, String>>) {
        removeAllViews()
        items.clear()

        itemList.forEachIndexed { index, pair ->
            val (iconRes, label) = pair
            val itemView = createItemView(index, iconRes, label)
            addView(itemView)
            items.add(itemView)
        }

        // Select the first item by default
        if (items.isNotEmpty()) {
            selectItem(0)
        }
    }

    private fun createItemView(index: Int, iconRes: Int, label: String): View {
        val itemLayout = LinearLayout(context).apply {
            orientation = VERTICAL
            gravity = Gravity.CENTER
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT).apply {
                topMargin = dpToPx(12)
                bottomMargin = dpToPx(12)
            }
        }

        val iconContainer = FrameLayout(context).apply {
            layoutParams = LayoutParams(dpToPx(48), dpToPx(48))
            background = ContextCompat.getDrawable(context, R.drawable.circle_background_unselected)
            isClickable = true
            isFocusable = true
            setOnClickListener { selectItem(index) }
        }

        val icon = AppCompatImageView(context).apply {
            setImageResource(iconRes)
            layoutParams = FrameLayout.LayoutParams(dpToPx(24), dpToPx(24), Gravity.CENTER)
        }

        iconContainer.addView(icon)

        val labelView = TextView(context).apply {
            text = label
            textSize = 12f
            gravity = Gravity.CENTER
            setTextColor(ContextCompat.getColor(context, R.color.black))
        }

        itemLayout.addView(iconContainer)
        itemLayout.addView(labelView)

        return itemLayout
    }

    private fun selectItem(index: Int) {
        if (index == selectedIndex) return

        items.forEachIndexed { i, view ->
            val iconContainer = (view as LinearLayout).getChildAt(0) as FrameLayout
            iconContainer.background = ContextCompat.getDrawable(
                context,
                if (i == index) R.drawable.circle_background_selected else R.drawable.circle_background_unselected
            )
        }

        selectedIndex = index
        onItemSelected?.invoke(index)
    }

    fun setOnItemSelectedListener(listener: (Int) -> Unit) {
        onItemSelected = listener
    }

    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }
}
