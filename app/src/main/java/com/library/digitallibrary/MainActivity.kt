package com.library.digitallibrary

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.appbar.MaterialToolbar
import com.library.digitallibrary.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()
        initView()
        setupEdgeToEdge()
    }

    private fun initView() {
        // Setup Toolbar
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        val logo = toolbar.findViewById<ImageView>(R.id.image_logo)

        // Better approach using scale type and max dimensions
        logo.scaleType = ImageView.ScaleType.FIT_CENTER

        if (isTablet()) {
            // For tablets - let the logo take more space naturally
            logo.adjustViewBounds = true
            logo.maxWidth = dpToPx(180)
            logo.maxHeight = dpToPx(60)  // Maintain aspect ratio
        } else {
            // For phones - more compact
            logo.adjustViewBounds = true
            logo.maxWidth = dpToPx(120)
            logo.maxHeight = dpToPx(40)
        }

        // Setup Navigation
        setupNavigation()
    }

    private fun setupNavigation() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        if (isTablet()) {
            // Tablet Layout: Show Navigation Rail, Hide Bottom Nav
            binding.bottomNav.visibility = View.GONE
            binding.navigationRail.visibility = View.VISIBLE

            val itemList = listOf(
                R.drawable.ic_home to "Home",
                R.drawable.ic_search to "Search",
                R.drawable.ic_download to "Downloaded",
                R.drawable.ic_more_horizontal to "More"
            )

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


            // Adjust fragment container to start after rail
            binding.navHostFragment.updateLayoutParams<ConstraintLayout.LayoutParams> {
                startToEnd = R.id.navigation_rail
            }
        } else {
            // Phone Layout: Show Bottom Nav, Hide Navigation Rail
            binding.navigationRail.visibility = View.GONE
            binding.bottomNav.visibility = View.VISIBLE
            binding.bottomNav.setupWithNavController(navController)

            // Reset fragment container constraints
            binding.navHostFragment.updateLayoutParams<ConstraintLayout.LayoutParams> {
                startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            }
        }
    }





    @RequiresApi(Build.VERSION_CODES.R)
    private fun setupEdgeToEdge() {
        // Handle edge-to-edge insets
        binding.root.setOnApplyWindowInsetsListener { view, insets ->
            val systemBars = insets.getInsets(android.view.WindowInsets.Type.systemBars())
            view.updatePadding(
                left = systemBars.left,
                right = systemBars.right
            )

            if (!isTablet()) {
                binding.bottomNav.updatePadding(
                    bottom = systemBars.bottom
                )
            }
            insets
        }
    }

    private fun isTablet(): Boolean {
        // Check if device is a tablet (sw600dp or larger)
        return resources.configuration.smallestScreenWidthDp >= 600
    }

    // Utility function for dp to px conversion
    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}
