package com.library.digitallibrary

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.updateLayoutParams
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.appbar.MaterialToolbar
import com.library.digitallibrary.databinding.ActivityMainBinding

/**
 * The main and only Activity in the application. It acts as the host for all Fragments
 * and manages shared UI components like the Toolbar and navigation.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    lateinit var navController: NavController
    private var isRailVisible = false
    private var isRailSetUp = false

    /**
     * A sealed class representing the different states the global Toolbar can be in.
     * This has been simplified, as many screens share the same toolbar configuration.
     */
    sealed class ToolbarState {
        /** Toolbar state for the home screen on a tablet, showing a menu icon. */
        object HomeTablet : ToolbarState()

        /** Toolbar state for the home screen on a phone, showing only the logo. */
        object HomePhone : ToolbarState()

        /**
         * Toolbar state for any inner screen (Details, Videos, Books, etc.).
         * It shows a back arrow and a centered title.
         */
        data class InnerScreen(val title: String) : ToolbarState()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
    }

    /**
     * Initializes the main views, setting up the Toolbar as the support action bar.
     */
    @SuppressLint("RestrictedApi")
    private fun initView() {
        setSupportActionBar(binding.toolbar.materialToolbar)
        // We still disable the default title, as we will set it manually.
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayShowHomeEnabled(true) // This hides the logo

        setupNavigation()
    }

    /**
     * Sets up the Jetpack Navigation component and its listeners.
     */
    private fun setupNavigation() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        if (!isTablet()) {
            setupBottomNavigationForPhone()
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (!isTablet()) {
                // For phones, hide the bottom navigation on any screen that isn't a main tab destination.
                val isMainDestination = destination.id == R.id.nav_home ||
                        destination.id == R.id.nav_search ||
                        destination.id == R.id.nav_download ||
                        destination.id == R.id.nav_more
                binding.bottomNav.visibility = if (isMainDestination) View.VISIBLE else View.GONE
            } else {
                // For tablets, hide the rail if it's open and we navigate to an inner screen.
                val isInnerScreen = destination.id == R.id.nav_detail || destination.id == R.id.nav_video
                if (isInnerScreen && isRailVisible) {
                    toggleRail()
                }
            }
        }
    }

    /**
     * Updates the global Toolbar's appearance. This version works with the simplified
     * toolbar.xml by setting properties directly on the toolbar, not on inner views.
     */
    fun updateToolbar(state: ToolbarState) {
        val toolbar = binding.toolbar.materialToolbar

        when (state) {
            is ToolbarState.HomePhone -> {
                toolbar.title = "" // No title on home screen
                supportActionBar?.setDisplayShowHomeEnabled(true)  // This shows the logo from app:logo
                supportActionBar?.setDisplayHomeAsUpEnabled(false)
                toolbar.logo = ContextCompat.getDrawable(this, R.drawable.ic_new_design_logo)

                toolbar.setNavigationOnClickListener(null)
            }

            is ToolbarState.HomeTablet -> {
                toolbar.title = "" // No title on home screen
                supportActionBar?.setDisplayShowHomeEnabled(true)  // This shows the logo from app:logo
                supportActionBar?.setDisplayHomeAsUpEnabled(true)
                toolbar.navigationIcon = ContextCompat.getDrawable(this, R.drawable.ic_menu)
                toolbar.logo = ContextCompat.getDrawable(this, R.drawable.ic_new_design_logo)
                toolbar.setNavigationOnClickListener { toggleRail() }
            }

            is ToolbarState.InnerScreen -> {
                // Set the title directly on the toolbar. The `app:titleCentered="true"`
                // attribute in the XML handles the centering automatically.
                toolbar.title = state.title
                supportActionBar?.setDisplayShowHomeEnabled(false) // This hides the logo
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
        // --- THIS IS THE RESTORED CODE ---
        binding.navHostFragment.updateLayoutParams<ConstraintLayout.LayoutParams> {
            startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
        }
    }

    private fun setupTabletRailNavigation() {
        val itemList = listOf(
            R.drawable.ic_house_50 to getString(R.string.nav_home),
            R.drawable.ic_surface_home to getString(R.string.nav_search),
            R.drawable.ic_group_home to getString(R.string.nav_downloaded),
            R.drawable.ic_more_home to getString(R.string.nav_more)
        )
        // Assuming your custom view `binding.navigationRail` has these methods.
        binding.navigationRail.setItems(itemList)
        binding.navigationRail.setOnItemSelectedListener { index ->
            val destination = when (index) {
                0 -> R.id.nav_home
                1 -> R.id.nav_search
                2 -> R.id.nav_download
                3 -> R.id.nav_more
                else -> R.id.nav_home
            }
            navController.navigate(destination)
        }
    }

    private fun toggleRail() {
        if (isRailVisible) {
            binding.navigationRail.animate().alpha(0f).setDuration(150).withEndAction {
                binding.navigationRail.visibility = View.GONE
                isRailVisible = false
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
}
