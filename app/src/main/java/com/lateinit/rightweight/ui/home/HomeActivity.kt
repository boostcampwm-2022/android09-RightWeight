package com.lateinit.rightweight.ui.home

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.lateinit.rightweight.R
import com.lateinit.rightweight.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {

    private var _binding: ActivityHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = DataBindingUtil.setContentView(this@HomeActivity, R.layout.activity_home)

        setSupportActionBar(binding.materialToolbar)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_container_view_home) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNavigation.setupWithNavController(navController)


        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.navigation_exercise,
                R.id.navigation_shared_routine_detail,
                R.id.navigation_routine_detail,
                R.id.navigation_routine_editor -> {
                    binding.bottomNavigation.visibility = View.GONE
                }
                else -> {
                    binding.bottomNavigation.visibility = View.VISIBLE
                }
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}