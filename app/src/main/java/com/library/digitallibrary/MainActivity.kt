package com.library.digitallibrary

import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.library.digitallibrary.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        initView()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.appbar_menu, menu)
        return true
    }

    private fun initView() {
        setSupportActionBar(binding.topAppbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        // Set nav_host
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController



        if (isTablet()) {
            binding.bottomNav.visibility = View.GONE
            binding.navigationRail.visibility = View.VISIBLE
            binding.coordinatorLayout.fitsSystemWindows = true
            binding.navigationRail.setupWithNavController(navController)
        } else {
            binding.navigationRail.visibility = View.GONE
            binding.bottomNav.visibility = View.VISIBLE
            binding.coordinatorLayout.fitsSystemWindows = false
            // Set bottom navigation with navController
            binding.bottomNav.setupWithNavController(navController)
        }

        // Set bottom navigation with navController
//        binding.bottomNav.setupWithNavController(navController)

        // Setup Navigation Drawer
        binding.navDrawer.setupWithNavController(navController)


        // Handle drawer toggle manually (optional)
        val toggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            binding.topAppbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.drawerArrowDrawable.color = ContextCompat.getColor(this, android.R.color.black)
        toggle.syncState()

        // Handle drawer item click
        binding.navDrawer.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_settings -> {
                    Toast.makeText(this, "Settings clicked", Toast.LENGTH_SHORT).show()
                }

                R.id.nav_about -> {
                    Toast.makeText(this, "About Us clicked", Toast.LENGTH_SHORT).show()
                }
            }
            binding.drawerLayout.closeDrawers()
            true
        }
    }

    private fun isTablet(): Boolean {
        return resources.configuration.smallestScreenWidthDp >= 600
    }
}
