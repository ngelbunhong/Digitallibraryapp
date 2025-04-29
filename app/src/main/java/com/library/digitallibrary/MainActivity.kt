package com.library.digitallibrary

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.appbar.MaterialToolbar
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

    private fun initView() {
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        // Set nav_host
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController



//        if (isTablet()) {
//            binding.bottomNav.visibility = View.GONE
//            binding.navigationRail.visibility = View.VISIBLE
//            binding.coordinatorLayout.fitsSystemWindows = true
//            binding.navigationRail.setupWithNavController(navController)
//        } else {
//            binding.navigationRail.visibility = View.GONE
//            binding.bottomNav.visibility = View.VISIBLE
//            binding.coordinatorLayout.fitsSystemWindows = false
//            // Set bottom navigation with navController
//            binding.bottomNav.setupWithNavController(navController)
//        }
        binding.bottomNav.setupWithNavController(navController)

        // Set bottom navigation with navController
//        binding.bottomNav.setupWithNavController(navController)
    }

    private fun isTablet(): Boolean {
        return resources.configuration.smallestScreenWidthDp >= 600
    }
}
