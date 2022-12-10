package com.lateinit.rightweight.ui.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Status
import com.google.android.material.snackbar.Snackbar
import com.lateinit.rightweight.R
import com.lateinit.rightweight.databinding.ActivityLoginBinding
import com.lateinit.rightweight.ui.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    lateinit var binding: ActivityLoginBinding
    private val loginViewModel: LoginViewModel by viewModels()

    private val options: GoogleSignInOptions by lazy {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail().build()
    }

    private val client: GoogleSignInClient by lazy {
        GoogleSignIn.getClient(applicationContext, options)
    }

    private val getGoogleLoginResultText =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                try {
                    val account = task.getResult(ApiException::class.java)
                    val idToken =
                        account.idToken ?: throw ApiException(Status.RESULT_INTERNAL_ERROR)
                    loginToFireBase(idToken)
                } catch (e: ApiException) {
                    Toast.makeText(this, "Failed Google Login", Toast.LENGTH_SHORT).show()
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this@LoginActivity, R.layout.activity_login)
        setLoginButtonListener()
        collectNetworkResponse()
    }

    private fun setLoginButtonListener() {
        binding.buttonGoogleLogin.setOnClickListener {
            login()
        }
    }

    private fun login() {
        val signInIntent = client.signInIntent
        getGoogleLoginResultText.launch(signInIntent)
    }

    private fun loginToFireBase(idToken: String) {
        loginViewModel.loginToFirebase(getString(R.string.google_api_key), idToken)
    }

    private fun moveToHomeActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun collectNetworkResponse() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                loginViewModel.networkResult.collect { networkResult ->
                    when (networkResult) {
                        NetworkState.NO_ERROR -> {}
                        NetworkState.BAD_INTERNET -> {
                            Snackbar.make(binding.root, "인터넷 상태 나쁨", Snackbar.LENGTH_LONG).show()
                        }
                        NetworkState.PARSE_ERROR -> {
                            Snackbar.make(binding.root, "파싱 오류", Snackbar.LENGTH_LONG).show()
                        }
                        NetworkState.WRONG_CONNECTION -> {
                            Snackbar.make(binding.root, "인터넷 연결 오류", Snackbar.LENGTH_LONG).show()
                        }
                        NetworkState.OTHER_ERROR -> {
                            Snackbar.make(binding.root, "예상치 못한 오류", Snackbar.LENGTH_LONG).show()
                        }
                        NetworkState.SUCCESS -> {
                            moveToHomeActivity()
                        }
                    }
                }
            }
        }
    }
}