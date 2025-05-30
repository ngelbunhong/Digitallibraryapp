package com.library.digitallibrary.ui.home

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.library.digitallibrary.R
import com.library.digitallibrary.data.adapter.AdsAdapter
import com.library.digitallibrary.databinding.FragmentHomeLibraryBinding

class HomeLibraryFragment : Fragment() {
    private var _binding: FragmentHomeLibraryBinding? = null
    private lateinit var viewModel: HomeViewModel
    private val binding get() = _binding!!
    private lateinit var adsAdapter: AdsAdapter
    private val autoScrollHelper = Handler(Looper.getMainLooper())
    private var currentPage = 0
    private val scrollerRunnable = object : Runnable {
        override fun run() {
            val itemCount = adsAdapter.itemCount
            if (itemCount == 0) return
            currentPage = (currentPage + 1) % itemCount
            binding.viewPagerAds.setCurrentItem(currentPage, true)
            autoScrollHelper.postDelayed(this, 5000)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeLibraryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]

        adsAdapter = AdsAdapter { ads ->
            Toast.makeText(requireContext(), "Clicked", Toast.LENGTH_SHORT).show()
        }

        binding.viewPagerAds.adapter = adsAdapter

        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.ads.observe(viewLifecycleOwner) { ads ->
            adsAdapter.submitList(ads)
            setupIndicators(ads.size)
        }
    }


    private fun setupIndicators(size: Int) {
        binding.indicatorLayout.removeAllViews()
        val indicators = Array(size) { ImageView(requireContext()) }

        for (i in indicators.indices) {
            indicators[i].apply {
                setImageResource(R.drawable.circle_selector)
                layoutParams = LinearLayout.LayoutParams(24, 24).apply {
                    marginStart = 8
                    marginEnd = 8
                }
                isSelected = i == 0 //first on is selected
                binding.indicatorLayout.addView(this)
            }
        }

        //page change listener to update selected indicator
        binding.viewPagerAds.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                for (i in 0 until binding.indicatorLayout.childCount) {
                    binding.indicatorLayout.getChildAt(i).isSelected = i == position
                }
            }

        })
    }

    override fun onResume() {
        super.onResume()
        autoScrollHelper.postDelayed(scrollerRunnable, 5000)
    }

    override fun onPause() {
        super.onPause()
        autoScrollHelper.removeCallbacks(scrollerRunnable)
    }

    override fun onDestroy() {
        super.onDestroy()
        autoScrollHelper.removeCallbacks(scrollerRunnable)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}