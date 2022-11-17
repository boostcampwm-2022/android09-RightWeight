package com.lateinit.rightweight.ui.home

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.lateinit.rightweight.R
import com.lateinit.rightweight.databinding.ActivityHomeBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private var _binding: ActivityHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = DataBindingUtil.setContentView(this@HomeActivity, R.layout.activity_home)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_container_view_home) as NavHostFragment
        navController = navHostFragment.navController

        binding.bottomNavigation.setupWithNavController(navController)

        setActionBar()
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.navigation_exercise -> {
                    supportActionBar?.hide()
                    binding.bottomNavigation.visibility = View.GONE
                }
                R.id.navigation_routine_detail -> {
                    supportActionBar?.show()
                    binding.bottomNavigation.visibility = View.GONE
                }
                R.id.navigation_shared_routine_detail,
                R.id.navigation_routine_editor -> {
                    supportActionBar?.show()
                    binding.bottomNavigation.visibility = View.GONE
                }
                else -> {
                    supportActionBar?.show()
                    binding.bottomNavigation.visibility = View.VISIBLE
                }
            }
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration)
    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawers()
        } else {
            super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logout -> {
                Toast.makeText(this, "로그아웃", Toast.LENGTH_SHORT).show()
            }
            R.id.withdraw -> {
                Toast.makeText(this, "회원 탈퇴", Toast.LENGTH_SHORT).show()
            }
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun setActionBar() {
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home,
                R.id.navigation_calendar,
                R.id.navigation_shared_routine,
                R.id.navigation_routine_management
            ),
            binding.drawerLayout
        )
        setSupportActionBar(binding.materialToolbar)
        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.navigationView.setNavigationItemSelectedListener(this)
        // disable drawer swipe gesture
        binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
    }
}