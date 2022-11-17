package com.lateinit.rightweight.ui.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.lateinit.rightweight.R
import com.lateinit.rightweight.databinding.ActivityLoginBinding
import com.lateinit.rightweight.ui.home.HomeActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.log

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    lateinit var binding: ActivityLoginBinding
    val viewModel: LoginViewModel by viewModels()

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
        Log.d(this.toString(), "onCreate")

        binding = ActivityLoginBinding.inflate(layoutInflater)

        setContentView(binding.root)

        binding.buttonGoogleLogin.setOnClickListener() {
            login()
        }
        supportActionBar?.hide()

        lifecycleScope.launch(){
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.loginResponse.collect() { loginResponse ->
                    Log.d("loginResponse", loginResponse.toString())
                    val nickname = loginResponse?.fullName.toString()
                }
            }
        }

        lifecycleScope.launch(){
            val lifecycleScopeInstance = this
            repeatOnLifecycle(Lifecycle.State.CREATED){
                val repeatOnLifecycleInstance = this
                Log.d("call", "")
                viewModel.networkResult.collect(){ networkResult ->
                    Log.d("NetworkResult", networkResult.toString())
                    Log.d("lifecycleScope", lifecycleScopeInstance.toString())
                    Log.d("repeatOnLifecycle", repeatOnLifecycleInstance.toString())
                    when(networkResult){
                        NetworkState.NO_ERROR ->{
                            val intent = Intent(baseContext, HomeActivity::class.java)
                            startActivity(intent)
                        }
                        NetworkState.WRONG_CONNECTION ->{
                            Snackbar.make(binding.root, "인터넷 연결 오류", Snackbar.LENGTH_LONG). show()
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
            viewModel.loginToFirebase(getString(R.string.google_api_key), idToken)
        }
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