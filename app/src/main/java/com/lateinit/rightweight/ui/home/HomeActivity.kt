package com.lateinit.rightweight.ui.home

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.google.gson.Gson
import com.lateinit.rightweight.R
import com.lateinit.rightweight.data.LoginResponse
import com.lateinit.rightweight.databinding.ActivityHomeBinding
import com.lateinit.rightweight.databinding.NavigationHeaderBinding
import com.lateinit.rightweight.ui.login.LoginActivity

class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private var _binding: ActivityHomeBinding? = null
    private val binding get() = _binding!!

    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(this.toString(), "onCreate")

        sharedPreferences = baseContext.getSharedPreferences(
            baseContext.getString(R.string.app_name),
            Context.MODE_PRIVATE
        )

        _binding = DataBindingUtil.setContentView(this@HomeActivity, R.layout.activity_home)

        setSupportActionBar(binding.materialToolbar)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_container_view_home) as NavHostFragment
        val navController = navHostFragment.navController

        binding.bottomNavigation.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.navigation_exercise -> {
                    binding.materialToolbar.visibility = View.GONE
                    binding.bottomNavigation.visibility = View.GONE
                }
                R.id.navigation_shared_routine_detail,
                R.id.navigation_routine_detail,
                R.id.navigation_routine_editor -> {
                    binding.materialToolbar.visibility = View.VISIBLE
                    binding.materialToolbar.setNavigationIcon(R.drawable.ic_back)
                    binding.materialToolbar.setNavigationOnClickListener {
                        onBackPressed()
                    }
                    binding.bottomNavigation.visibility = View.GONE
                }
                else -> {
                    binding.materialToolbar.visibility = View.VISIBLE
                    binding.materialToolbar.setNavigationIcon(R.drawable.ic_menu)
                    binding.materialToolbar.setNavigationOnClickListener {
                        binding.drawerLayout.openDrawer(GravityCompat.START)
                    }
                    binding.bottomNavigation.visibility = View.VISIBLE
                }
            }
        }

        val headerBinding = NavigationHeaderBinding.bind(binding.navigationView.getHeaderView(0))
        val loginResponse = Gson().fromJson(
            sharedPreferences.getString("loginResponse", null),
            LoginResponse::class.java
        )
        headerBinding.loginResponse = loginResponse

        binding.navigationView.setNavigationItemSelectedListener(this)
        // disable drawer swipe gesture
        binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
    }

//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        when (item.itemId) {
//            android.R.id.home -> {
//                binding.drawerLayout.openDrawer(GravityCompat.START)
//            }
//        }
//        return super.onOptionsItemSelected(item)
//    }

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
                sharedPreferences.edit().putString("loginResponse", null)
                    .apply()
                val intent = Intent(baseContext, LoginActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
            }
            R.id.withdraw -> {
                Toast.makeText(this, "회원 탈퇴", Toast.LENGTH_SHORT).show()
            }
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onResume() {
        super.onResume()
        Log.d(this.toString(), "onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.d(this.toString(), "onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.d(this.toString(), "onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(this.toString(), "onDestroy")
    }


}