package com.lateinit.rightweight.ui.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lateinit.rightweight.data.LoginResponse
import com.lateinit.rightweight.data.repository.LoginRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.net.SocketException
import java.net.UnknownHostException
import javax.inject.Inject


@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginRepository: LoginRepository
) : ViewModel() {

    private val _networkResult = MutableStateFlow<NetworkState?>(null)
    val networkResult = _networkResult.asStateFlow()

    private val _loginResponse = MutableStateFlow<LoginResponse?>(null)
    val loginResponse = _loginResponse.asStateFlow()

    val networkExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
        when (throwable) {
            is SocketException ->  _networkResult.value = NetworkState.BAD_INTERNET
            is HttpException ->  _networkResult.value = NetworkState.PARSE_ERROR
            is UnknownHostException ->  _networkResult.value = NetworkState.WRONG_CONNECTION
            else ->  _networkResult.value = NetworkState.OTHER_ERROR
        }
    }

    fun loginToFirebase(key: String, token: String) {
        viewModelScope.launch(networkExceptionHandler) {
            _loginResponse.value = null
            _networkResult.value = null
            _loginResponse.value = loginRepository.loginToFirebase(key, token)
            _networkResult.value = NetworkState.NO_ERROR
        }
    }
}

enum class NetworkState() {
    NO_ERROR, BAD_INTERNET, PARSE_ERROR, WRONG_CONNECTION, OTHER_ERROR
}