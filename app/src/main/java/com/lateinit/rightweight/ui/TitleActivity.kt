package com.lateinit.rightweight.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.lateinit.rightweight.R
import com.lateinit.rightweight.databinding.ActivityTitleBinding
import com.lateinit.rightweight.ui.login.LoginActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TitleActivity: AppCompatActivity() {

    private var binding: ActivityTitleBinding? = null

    private val options: GoogleSignInOptions by lazy {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail().build()
    }

    private val client: GoogleSignInClient by lazy {
        GoogleSignIn.getClient(applicationContext, options)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this@TitleActivity, R.layout.activity_title)

        checkLoginBefore()
    }

    private fun checkLoginBefore() {
        lifecycleScope.launch {
            delay(1_000L)
            client.silentSignIn().addOnCompleteListener {
                it.addOnSuccessListener{
                    moveToHomeActivity()
                }
                it.addOnFailureListener{
                    moveToLoginActivity()
                }
            }
        }
    }

    private fun moveToLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun moveToHomeActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }


}