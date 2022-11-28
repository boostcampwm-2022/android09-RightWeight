package com.lateinit.rightweight.ui.home

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.DialogFragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.*
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
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

    private lateinit var binding: ActivityHomeBinding
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private val dialog: CommonDialogFragment by lazy {
        CommonDialogFragment()
    }
    val userViewModel: UserViewModel by viewModels()
    private val client: GoogleSignInClient by lazy {
        val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail().build()
        GoogleSignIn.getClient(applicationContext, options)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this@HomeActivity, R.layout.activity_home)

        setActionBar()
        setNavController()
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
                dialog.show(
                    supportFragmentManager,
                    LOGOUT_DIALOG_TAG,
                    R.string.logout_message
                )
            }
            R.id.withdraw -> {
                dialog.show(
                    supportFragmentManager,
                    WITHDRAW_DIALOG_TAG,
                    R.string.withdraw_message
                )
            }
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onDialogPositiveClick(dialog: DialogFragment) {
        when (dialog.tag) {
            LOGOUT_DIALOG_TAG -> {
                logout()
            }
            WITHDRAW_DIALOG_TAG -> {
                withdraw()
            }
        }
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
                R.id.navigation_routine_detail,
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

        // set drawer header
        val headerBinding = NavigationHeaderBinding.bind(binding.navigationView.getHeaderView(0))

        userViewModel.getLoginResponse()
        userViewModel.loginResponse.observe(this) { loginResponse ->
            headerBinding.loginResponse = loginResponse
        }

        binding.navigationView.setNavigationItemSelectedListener(this)

        // disable drawer swipe gesture
        binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
    }

    private fun logout() {
        userViewModel.setUser(null)
        userViewModel.setLoginResponse(null)
        client.signOut()
        val intent = Intent(baseContext, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
    }

    private fun withdraw() {
        Toast.makeText(this, "회원 탈퇴", Toast.LENGTH_SHORT).show()
    }

    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                val focusView = currentFocus
                if (focusView is EditText) {
                    val outRect = Rect()
                    focusView.getGlobalVisibleRect(outRect)
                    if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                        focusView.clearFocus()
                        hideKeyboard(focusView)
                    }
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }

    private fun hideKeyboard(focusView: View) {
        val inputMethodManager: InputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(focusView.windowToken, 0)
    }

    fun navigateBottomNav(@IdRes itemId: Int) {
        val item = binding.bottomNavigation.menu.findItem(itemId)
        NavigationUI.onNavDestinationSelected(item, navController)
    }
}