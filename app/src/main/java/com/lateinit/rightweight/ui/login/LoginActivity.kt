package com.lateinit.rightweight.ui.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.lateinit.rightweight.R
import com.lateinit.rightweight.data.model.User
import com.lateinit.rightweight.databinding.ActivityLoginBinding
import com.lateinit.rightweight.ui.home.HomeActivity
import com.lateinit.rightweight.ui.home.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    lateinit var binding: ActivityLoginBinding
    val loginViewModel: LoginViewModel by viewModels()
    val userViewModel: UserViewModel by viewModels()

    val getGoogleLoginResultText =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                var account: GoogleSignInAccount? = null
                try {
                    account = task.getResult(ApiException::class.java)
                    loginToFireBase(account?.idToken)
                } catch (e: ApiException) {
                    Toast.makeText(this, "Failed Google Login", Toast.LENGTH_SHORT).show()
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail().build()
        val client = GoogleSignIn.getClient(applicationContext, options)

        client.silentSignIn().addOnSuccessListener {
            val intent = Intent(baseContext, HomeActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        }

        binding = ActivityLoginBinding.inflate(layoutInflater)

        setContentView(binding.root)

        binding.buttonGoogleLogin.setOnClickListener() {
            login()
        }
        supportActionBar?.hide()

        lifecycleScope.launch() {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                loginViewModel.loginResponse.collect() { loginResponse ->
                    if (loginResponse != null) {
                        userViewModel.setLoginResponse(loginResponse)

                        userViewModel.userInfo.value ?: run {
                            loginResponse.localId?.let { localId ->
                                userViewModel.setUser(User(localId, null, null))
                            }
                        }
                    }
                }
            }
        }

        lifecycleScope.launch() {
            val lifecycleScopeInstance = this
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                val repeatOnLifecycleInstance = this
                loginViewModel.networkResult.collect() { networkResult ->
                    when (networkResult) {
                        NetworkState.NO_ERROR -> {
                            val intent = Intent(baseContext, HomeActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            startActivity(intent)
                        }
                        NetworkState.WRONG_CONNECTION -> {
                            Snackbar.make(binding.root, "인터넷 연결 오류", Snackbar.LENGTH_LONG).show()
                        }
                        else -> {}
                    }
                }
            }
        }
    }

    fun login() {
        val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail().build()
        val signInIntent = GoogleSignIn.getClient(this, options).signInIntent

        getGoogleLoginResultText.launch(signInIntent)
    }

    fun loginToFireBase(idToken: String?) {
        idToken?.let {
            loginViewModel.loginToFirebase(getString(R.string.google_api_key), idToken)
        }
    }
}