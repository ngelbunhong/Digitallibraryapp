package com.library.digitallibrary

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.util.TypedValueCompat.dpToPx
import androidx.core.view.updateLayoutParams
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigationrail.NavigationRailView
import com.library.digitallibrary.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    lateinit var navController: NavController
    private var isRailVisible = false
    private var isRailSetUp = false

    // Defines the states for the toolbar that fragments will request
    sealed class ToolbarState {
        object HomeTablet : ToolbarState()
        object HomePhone : ToolbarState()
        data class DetailScreen(val title: String) : ToolbarState()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
    }

    @SuppressLint("RestrictedApi")
    private fun initView() {
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        setupNavigation()
    }

    private fun setupNavigation() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        if (!isTablet()) {
            setupBottomNavigationForPhone()
        }

        // --- THIS IS THE NEW LOGIC TO HIDE THE RAIL ---
        // Add a listener that fires every time we navigate to a new screen.
        navController.addOnDestinationChangedListener { _, destination, _ ->
            // Check if the new screen is the DetailFragment
            if (destination.id == R.id.detailFragment) {
                // If the rail is currently visible, call toggleRail() to hide it.
                if (isRailVisible) {
                    toggleRail()
                }
            }
        }
    }

    fun updateToolbar(state: ToolbarState) {
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        val logo = toolbar.findViewById<ImageView>(R.id.image_logo)
        val centeredTitle = toolbar.findViewById<TextView>(R.id.toolbar_title_centered)

        when (state) {
            is ToolbarState.HomePhone -> {
                logo.visibility = View.VISIBLE
                centeredTitle.visibility = View.GONE
                supportActionBar?.setDisplayHomeAsUpEnabled(false)
                toolbar.setNavigationOnClickListener(null)
            }

            is ToolbarState.HomeTablet -> {
                logo.visibility = View.VISIBLE
                centeredTitle.visibility = View.GONE
                supportActionBar?.setDisplayHomeAsUpEnabled(true)
                toolbar.navigationIcon = ContextCompat.getDrawable(this, R.drawable.ic_menu)
                toolbar.setNavigationOnClickListener { toggleRail() }
            }

            is ToolbarState.DetailScreen -> {
                logo.visibility = View.GONE
                centeredTitle.visibility = View.VISIBLE
                centeredTitle.text = state.title
                binding.bottomNav.visibility = View.GONE
                supportActionBar?.setDisplayHomeAsUpEnabled(true)
                toolbar.navigationIcon = ContextCompat.getDrawable(this, R.drawable.ic_arrow_back)
                toolbar.setNavigationOnClickListener { onSupportNavigateUp() }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    fun isTablet(): Boolean {
        return resources.configuration.smallestScreenWidthDp >= 600
    }

    private fun setupBottomNavigationForPhone() {
        binding.navigationRail.visibility = View.GONE
        binding.bottomNav.visibility = View.VISIBLE
        binding.bottomNav.setupWithNavController(navController)
        binding.bottomNav.itemIconTintList = null
        binding.navHostFragment.updateLayoutParams<ConstraintLayout.LayoutParams> {
            startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
        }
    }

    private fun toggleRail() {
        if (isRailVisible) {
            binding.navigationRail.animate().alpha(0f).setDuration(150).withEndAction {
                binding.navigationRail.visibility = View.GONE; isRailVisible = false
            }.start()
        } else {
            if (!isRailSetUp) {
                setupTabletRailNavigation()
                isRailSetUp = true
            }
            binding.navigationRail.alpha = 0f
            binding.navigationRail.visibility = View.VISIBLE
            binding.navigationRail.animate().alpha(1f).setDuration(150).withEndAction {
                isRailVisible = true
            }.start()
        }
    }

    private fun setupTabletRailNavigation() {
        val itemList = listOf(
            R.drawable.ic_house_50 to getString(R.string.nav_home),
            R.drawable.ic_surface_home to getString(R.string.nav_search),
            R.drawable.ic_group_home to getString(R.string.nav_downloaded),
            R.drawable.ic_more_home to getString(R.string.nav_more)
        )
        binding.navigationRail.setItems(itemList)
        binding.navigationRail.setOnItemSelectedListener { index ->
            val destination = when (index) {
                0 -> R.id.nav_home;
                1 -> R.id.nav_search;
                2 -> R.id.nav_download;
                3 -> R.id.nav_more;
                else -> R.id.nav_home
            }
            navController.navigate(destination)
        }
    }
}