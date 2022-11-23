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
import androidx.fragment.app.DialogFragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.navigation.NavigationView
import com.google.gson.Gson
import com.lateinit.rightweight.R
import com.lateinit.rightweight.data.LoginResponse
import com.lateinit.rightweight.databinding.ActivityHomeBinding
import com.lateinit.rightweight.databinding.NavigationHeaderBinding
import com.lateinit.rightweight.ui.home.dialog.CommonDialogFragment
import com.lateinit.rightweight.ui.home.dialog.CommonDialogFragment.Companion.LOGOUT_DIALOG_TAG
import com.lateinit.rightweight.ui.home.dialog.CommonDialogFragment.Companion.WITHDRAW_DIALOG_TAG
import com.lateinit.rightweight.ui.login.LoginActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    CommonDialogFragment.NoticeDialogListener {

    private var _binding: ActivityHomeBinding? = null
    val binding get() = _binding!!
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private val dialog: CommonDialogFragment by lazy {
        CommonDialogFragment()
    }
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("HomeActivity", "onCreate")
        Log.d("HomeActivity", this.toString())

        sharedPreferences = baseContext.getSharedPreferences(
            baseContext.getString(R.string.app_name),
            Context.MODE_PRIVATE
        )

        _binding = DataBindingUtil.setContentView(this@HomeActivity, R.layout.activity_home)

        setActionBar()
        setNavController()

        val headerBinding = NavigationHeaderBinding.bind(binding.navigationView.getHeaderView(0))
        val loginResponse = Gson().fromJson(
            sharedPreferences.getString("loginResponse", null),
            LoginResponse::class.java
        )
        headerBinding.loginResponse = loginResponse
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
                logout()
            }
            R.id.withdraw -> {
                withdraw()
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

    private fun setNavController() {
        //Log.d("HomeActivity", "setNavController")
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_container_view_home) as NavHostFragment
        navController = navHostFragment.navController
        binding.bottomNavigation.setupWithNavController(navController)
        setupActionBarWithNavController(navController, appBarConfiguration)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.navigation_exercise -> {
                    //FIXME hide를 했을 때 notification -> homeFragment -> exerciseFragment 넘어올 때 toolbar 사라지지 않음
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

    private fun setActionBar() {
        //Log.d("HomeActivity", "setActionBar")
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home,
                R.id.navigation_calendar,
                R.id.navigation_shared_routine,
                R.id.navigation_routine_management,
                R.id.navigation_exercise
            ),
            binding.drawerLayout
        )
        setSupportActionBar(binding.materialToolbar)
        binding.navigationView.setNavigationItemSelectedListener(this)

        // disable drawer swipe gesture
        binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
    }

    private fun logout() {
        dialog.show(
            supportFragmentManager,
            LOGOUT_DIALOG_TAG,
            R.string.logout_message
        )
    }

    private fun withdraw() {
        dialog.show(
            supportFragmentManager,
            WITHDRAW_DIALOG_TAG,
            R.string.withdraw_message
        )
    }

    override fun onDialogPositiveClick(dialog: DialogFragment) {
        when (dialog.tag) {
            LOGOUT_DIALOG_TAG -> {
                sharedPreferences.edit().putString("loginResponse", null).apply()
                sharedPreferences.edit().putString("userInfo", null).apply()
                val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail().build()
                val client = GoogleSignIn.getClient(applicationContext, options)
                client.signOut()
                val intent = Intent(baseContext, LoginActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
            }
            WITHDRAW_DIALOG_TAG -> {
                Toast.makeText(this, "회원 탈퇴", Toast.LENGTH_SHORT).show()
            }
        }
    }
}