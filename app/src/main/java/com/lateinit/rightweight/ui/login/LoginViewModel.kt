package com.lateinit.rightweight.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lateinit.rightweight.data.model.local.User
import com.lateinit.rightweight.data.model.remote.LoginResponse
import com.lateinit.rightweight.data.repository.LoginRepository
import com.lateinit.rightweight.data.repository.UserRepository
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
    private val loginRepository: LoginRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _networkResult = MutableStateFlow(NetworkState.NO_ERROR)
    val networkResult = _networkResult.asStateFlow()

    private val networkExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        when (throwable) {
            is SocketException -> _networkResult.value = NetworkState.BAD_INTERNET
            is HttpException -> _networkResult.value = NetworkState.PARSE_ERROR
            is UnknownHostException -> _networkResult.value = NetworkState.WRONG_CONNECTION
            else -> _networkResult.value = NetworkState.OTHER_ERROR
        }
    }

    fun loginToFirebase(key: String, token: String) {
        viewModelScope.launch(networkExceptionHandler) {
            saveUser(loginRepository.login(key, token))
            _networkResult.value = NetworkState.SUCCESS
        }
    }

    private suspend fun saveUser(loginResponse: LoginResponse) {
        with(loginResponse) {
            userRepository.saveUser(
                User(localId, "", "", "", email, displayName, photoUrl, idToken, refreshToken)
            )
        }
    }
}

enum class NetworkState {
    NO_ERROR, BAD_INTERNET, PARSE_ERROR, WRONG_CONNECTION, OTHER_ERROR, SUCCESS
}