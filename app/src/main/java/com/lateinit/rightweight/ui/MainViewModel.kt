package com.lateinit.rightweight.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lateinit.rightweight.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    userRepository: UserRepository
) : ViewModel() {

    val userInfo = userRepository.getUser().stateIn(viewModelScope, SharingStarted.Eagerly, null)

    fun backup() {
        TODO("Not yet implemented")
    }
}