package com.lateinit.rightweight.ui.login

import androidx.lifecycle.ViewModel
import com.lateinit.rightweight.data.repository.LoginRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginRepository: LoginRepository
): ViewModel() {

}