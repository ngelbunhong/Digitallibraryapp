package com.library.digitallibrary.data.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.library.digitallibrary.data.models.ads.Ads
import com.library.digitallibrary.databinding.AdsItemBinding

class AdsAdapter(private val onAdClick: (Ads) -> Unit) : ListAdapter<Ads, AdsAdapter.AdsViewHolder>(
    AdsDiffCallback()
) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AdsViewHolder {
        val binding = AdsItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AdsViewHolder(binding, onAdClick)
    }

    override fun onBindViewHolder(
        holder: AdsViewHolder,
        position: Int
    ) {
        holder.bind(getItem(position))
    }

    class AdsViewHolder(
        private val binding: AdsItemBinding,
        private val onAdsClick: (Ads) -> Unit
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(ads: Ads) {
            if (ads.imageResId != null) {
                binding.adsSlide.setImageResource(ads.imageResId)
            } else if (!ads.imageUrl.isNullOrEmpty()) {
                Glide.with(binding.adsSlide.context)
                    .load(ads.imageUrl)
                    .into(binding.adsSlide)
            }
            binding.root.setOnClickListener {
                onAdsClick(ads)
            }
        }

    }
}


class AdsDiffCallback : DiffUtil.ItemCallback<Ads>() {
    override fun areItemsTheSame(
        oldItem: Ads,
        newItem: Ads
    ): Boolean = oldItem.id == newItem.id

    override fun areContentsTheSame(
        oldItem: Ads,
        newItem: Ads
    ): Boolean = oldItem == newItem
}