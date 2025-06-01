package com.library.digitallibrary.data.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.library.digitallibrary.data.models.ads.Ads
import com.library.digitallibrary.databinding.CardItemBinding

class CardItemAdapter(private val onClicked: (Ads) -> Unit) :
    ListAdapter<Ads, CardItemAdapter.CardItemViewHolder>(AdsDiffCallback()) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CardItemViewHolder {
        val binding = CardItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CardItemViewHolder(binding, onClicked)
    }

    override fun onBindViewHolder(
        holder: CardItemViewHolder,
        position: Int
    ) {
        holder.bind(getItem(position))
    }

    class CardItemViewHolder(
        private val binding: CardItemBinding,
        private val cardItemClicked: (Ads) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(cardItem: Ads) {
            binding.imageItem.setImageResource(cardItem.imageResId!!)
            binding.titleText.setText(cardItem.titleResId!!)
            binding.root.setOnClickListener {
                cardItemClicked(cardItem)
            }
        }
    }
}