package com.library.digitallibrary

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.library.digitallibrary.databinding.ActivityMainBinding

/**
 * The main and only Activity hosting all fragments and managing global UI components,
 * like the Toolbar and Navigation (Bottom Navigation or Navigation Rail).
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    lateinit var navController: NavController

    private var isRailVisible = false
    private var isRailSetUp = false

    /**
     * Sealed class to represent different toolbar states in the app.
     */
    sealed class ToolbarState {
        /** Toolbar for the home screen on tablets with menu icon. */
        object HomeTablet : ToolbarState()

        /** Toolbar for the home screen on phones showing only logo. */
        object HomePhone : ToolbarState()

        /** Toolbar for inner screens with back arrow and centered title. */
        data class InnerScreen(val title: String) : ToolbarState()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configureSystemBars()
        applyWindowInsets()

        initView()
    }

    /**
     * Configure window to draw behind system bars, handle cutouts,
     * and set light/dark system bar icon colors depending on theme.
     */
    private fun configureSystemBars() {
        WindowCompat.setDecorFitsSystemWindows(window, false)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.attributes.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }

        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        val isLightTheme =
            resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK != Configuration.UI_MODE_NIGHT_YES

        windowInsetsController.isAppearanceLightStatusBars = isLightTheme
        windowInsetsController.isAppearanceLightNavigationBars = isLightTheme

        // Initially hide navigation bar for immersive experience
        hideSystemBars()
    }

    /**
     * Apply system bar insets as padding to the app bar layout to avoid
     * overlap with system status bar.
     */
    private fun applyWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, insets ->
            val systemBarsInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            binding.appbarLayout.updatePadding(top = systemBarsInsets.top)
            WindowInsetsCompat.CONSUMED
        }

        // Transparent navigation bar for seamless look
        window.navigationBarColor = android.graphics.Color.TRANSPARENT
    }

    /**
     * Initialize toolbar and navigation components.
     */
    @SuppressLint("RestrictedApi")
    private fun initView() {
        setSupportActionBar(binding.toolbar.materialToolbar)
        supportActionBar?.apply {
            setDisplayShowTitleEnabled(false) // We control title manually
            setDisplayShowHomeEnabled(true)   // Show logo by default
        }

        setupNavigation()
    }

    /**
     * Setup Jetpack Navigation and configure navigation UI based on device type.
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
                // Show bottom nav only on main tab destinations
                val mainTabs = setOf(R.id.nav_home, R.id.nav_search, R.id.nav_download, R.id.nav_more)
                binding.bottomNav.visibility = if (destination.id in mainTabs) View.VISIBLE else View.GONE
            } else {
                // Hide navigation rail on inner screens
                val innerScreens = setOf(R.id.nav_detail, R.id.nav_video)
                if (destination.id in innerScreens && isRailVisible) {
                    toggleRail()
                }
            }
        }
    }

    /**
     * Updates the toolbar appearance based on [ToolbarState].
     */
    fun updateToolbar(state: ToolbarState) {
        val toolbar = binding.toolbar.materialToolbar
        when (state) {
            is ToolbarState.HomePhone -> {
                toolbar.title = ""
                supportActionBar?.apply {
                    setDisplayShowHomeEnabled(true)
                    setDisplayHomeAsUpEnabled(false)
                }
                toolbar.logo = ContextCompat.getDrawable(this, R.drawable.ic_new_design_logo)
                toolbar.setNavigationOnClickListener(null)
            }

            is ToolbarState.HomeTablet -> {
                toolbar.title = ""
                supportActionBar?.apply {
                    setDisplayShowHomeEnabled(true)
                    setDisplayHomeAsUpEnabled(true)
                }
                toolbar.navigationIcon = ContextCompat.getDrawable(this, R.drawable.ic_menu)
                toolbar.logo = ContextCompat.getDrawable(this, R.drawable.ic_new_design_logo)
                toolbar.setNavigationOnClickListener { toggleRail() }
            }

            is ToolbarState.InnerScreen -> {
                toolbar.title = state.title
                supportActionBar?.apply {
                    setDisplayShowHomeEnabled(false)
                    setDisplayHomeAsUpEnabled(true)
                }
                toolbar.navigationIcon = ContextCompat.getDrawable(this, R.drawable.ic_arrow_back)
                toolbar.setNavigationOnClickListener { onSupportNavigateUp() }
            }
        }
    }

    /**
     * Handles back navigation for the toolbar's navigation icon.
     */
    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    /**
     * Utility function to detect if the device is a tablet (sw >= 600dp).
     */
    fun isTablet(): Boolean = resources.configuration.smallestScreenWidthDp >= 600

    /**
     * Setup bottom navigation for phone devices.
     */
    private fun setupBottomNavigationForPhone() {
        binding.navigationRail.visibility = View.GONE
        binding.bottomNav.apply {
            visibility = View.VISIBLE
            setupWithNavController(navController)
            itemIconTintList = null
        }
        // Adjust nav host fragment to fill width between start and end
        binding.navHostFragment.updateLayoutParams<ConstraintLayout.LayoutParams> {
            startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
        }
    }

    /**
     * Setup navigation rail for tablets.
     */
    private fun setupTabletRailNavigation() {
        val itemList = listOf(
            R.drawable.ic_house_50 to getString(R.string.nav_home),
            R.drawable.ic_surface_home to getString(R.string.nav_search),
            R.drawable.ic_group_home to getString(R.string.nav_downloaded),
            R.drawable.ic_more_home to getString(R.string.nav_more)
        )
        // Assuming your custom view supports these methods
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

    /**
     * Show/hide the tablet navigation rail with animation.
     */
    private fun toggleRail() {
        if (isRailVisible) {
            binding.navigationRail.animate()
                .alpha(0f)
                .setDuration(150)
                .withEndAction {
                    binding.navigationRail.visibility = View.GONE
                    isRailVisible = false
                }.start()
        } else {
            if (!isRailSetUp) {
                setupTabletRailNavigation()
                isRailSetUp = true
            }
            binding.navigationRail.apply {
                alpha = 0f
                visibility = View.VISIBLE
                animate()
                    .alpha(1f)
                    .setDuration(150)
                    .withEndAction { isRailVisible = true }
                    .start()
            }
        }
    }

    /**
     * Hides the system navigation bar for immersive UI.
     */
    private fun hideSystemBars() {
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)

        // Hide only navigation bars (bottom)
        windowInsetsController.hide(WindowInsetsCompat.Type.navigationBars())

        // Keep status bar visible with light icons if applicable
        windowInsetsController.isAppearanceLightStatusBars = true

        // Allow swipe to show bars temporarily
        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        // Make navigation bar transparent
        window.navigationBarColor = android.graphics.Color.TRANSPARENT
    }

    /**
     * Re-apply system bars hiding on resume to maintain immersive mode.
     */
    override fun onResume() {
        super.onResume()
        hideSystemBars()
    }
}
