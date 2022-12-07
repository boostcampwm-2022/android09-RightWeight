package com.lateinit.rightweight.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lateinit.rightweight.data.repository.LoginRepository
import com.lateinit.rightweight.data.repository.UserRepository
import com.lateinit.rightweight.ui.login.NetworkState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.net.SocketException
import java.net.UnknownHostException
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    userRepository: UserRepository,
    private val loginRepository: LoginRepository
) : ViewModel() {

    val userInfo = userRepository.getUser().stateIn(viewModelScope, SharingStarted.Eagerly, null)

    private val _networkState = MutableSharedFlow<NetworkState>()
    val networkState = _networkState.asSharedFlow()


    private val networkExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        viewModelScope.launch {
            when (throwable) {
                is SocketException -> sendNetworkResultEvent(NetworkState.BAD_INTERNET)
                is HttpException -> sendNetworkResultEvent(NetworkState.PARSE_ERROR)
                is UnknownHostException -> sendNetworkResultEvent(NetworkState.WRONG_CONNECTION)
                else -> sendNetworkResultEvent(NetworkState.OTHER_ERROR)
            }
        }
    }

    fun deleteAccount(key: String, idToken: String) {
        viewModelScope.launch(networkExceptionHandler) {
            loginRepository.deleteAccount(key, idToken)
            sendNetworkResultEvent(NetworkState.SUCCESS)
        }
    }

    private suspend fun sendNetworkResultEvent(state: NetworkState) {
        _networkState.emit(state)
    }
}