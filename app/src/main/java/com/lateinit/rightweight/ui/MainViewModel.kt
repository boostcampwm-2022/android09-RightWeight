package com.lateinit.rightweight.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lateinit.rightweight.data.repository.LoginRepository
import com.lateinit.rightweight.data.repository.UserRepository
import com.lateinit.rightweight.ui.login.NetworkState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.*
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

    private val _network = MutableSharedFlow<NetworkState>()
    val network = _network.asSharedFlow()


    private val networkExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        when (throwable) {
            is SocketException -> netWorkResultEvent(NetworkState.BAD_INTERNET)
            is HttpException -> netWorkResultEvent(NetworkState.PARSE_ERROR)
            is UnknownHostException -> netWorkResultEvent(NetworkState.WRONG_CONNECTION)
            else -> netWorkResultEvent(NetworkState.OTHER_ERROR)
        }
    }

    fun deleteAccount(key: String, idToken: String) {
        viewModelScope.launch(networkExceptionHandler) {
            loginRepository.deleteAccount(key, idToken)
            netWorkResultEvent(NetworkState.SUCCESS)
        }
    }

    private fun netWorkResultEvent(state: NetworkState) {
        viewModelScope.launch {
            _network.emit(state)
        }
    }
}