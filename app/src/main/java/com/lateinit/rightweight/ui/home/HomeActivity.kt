package com.lateinit.rightweight.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
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
import com.lateinit.rightweight.R
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
    val userViewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = DataBindingUtil.setContentView(this@HomeActivity, R.layout.activity_home)

        setActionBar()
        setNavController()

        userViewModel.getLoginResponse()
        userViewModel.loginResponse.observe(this) { loginResponse ->
            NavigationHeaderBinding.bind(binding.navigationView.getHeaderView(0)).also {
                it.loginResponse = loginResponse
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
                logout()
            }
            R.id.withdraw -> {
                withdraw()
            }
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun setNavController() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_container_view_home) as NavHostFragment
        navController = navHostFragment.navController
        binding.bottomNavigation.setupWithNavController(navController)
        setupActionBarWithNavController(navController, appBarConfiguration)

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

    private fun setActionBar() {
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
                userViewModel.setUser(null)
                userViewModel.setLoginResponse(null)
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