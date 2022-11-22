package com.lateinit.rightweight.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lateinit.rightweight.data.model.User
import com.lateinit.rightweight.data.repository.RoutineRepository
import com.lateinit.rightweight.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val routineRepository: RoutineRepository
) : ViewModel() {

    private val _userInfo = MutableLiveData<User>()
    val userInfo: LiveData<User> get() = _userInfo
    private val _routineTitle = MutableLiveData<String>()
    val routineTitle: LiveData<String> get() = _routineTitle

    init {
        getUser()
    }

    fun getUser() {
        viewModelScope.launch {
            val user = userRepository.getUser()
            _userInfo.postValue(user)
        }
    }

    fun getRoutineById() {
        val routineId = userInfo.value?.routineId ?: run {
            _routineTitle.value = "루틴을 설정해주세요."
            return
        }
        viewModelScope.launch {
            val routine = routineRepository.getRoutineById(routineId)
            _routineTitle.postValue(routine.title)
        }
    }
}