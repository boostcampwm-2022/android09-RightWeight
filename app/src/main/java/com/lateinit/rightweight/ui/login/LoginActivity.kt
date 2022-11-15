package com.lateinit.rightweight.ui.login

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.lateinit.rightweight.R
import com.lateinit.rightweight.databinding.ActivityLoginBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    lateinit var binding: ActivityLoginBinding
    val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)

        setContentView(binding.root)
        login()
        supportActionBar?.hide()
    }

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

    fun login() {
        val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail().build()
        val signInIntent = GoogleSignIn.getClient(this, options).signInIntent

        getGoogleLoginResultText.launch(signInIntent)
    }

    fun loginToFireBase(idToken: String?) {
        idToken?.let{
            viewModel.loginToFirebase(getString(R.string.google_api_key), idToken)
        }
    }
}