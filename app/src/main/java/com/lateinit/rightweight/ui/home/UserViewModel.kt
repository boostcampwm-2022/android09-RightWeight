package com.lateinit.rightweight.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lateinit.rightweight.data.database.entity.Day
import com.lateinit.rightweight.data.database.entity.Routine
import com.lateinit.rightweight.data.model.User
import com.lateinit.rightweight.data.repository.RoutineRepository
import com.lateinit.rightweight.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val routineRepository: RoutineRepository
) : ViewModel() {

    private val _userInfo = MutableLiveData<User>()
    val userInfo: LiveData<User> get() = _userInfo
    private val _routine = MutableLiveData<Routine>()
    val routine: LiveData<Routine> get() = _routine

    init {
        getUser()
    }

    fun setUser(routineId: String) {
        val user = userInfo.value ?: return
        viewModelScope.launch {
            val days: List<Day> = routineRepository.getDaysByRoutineId(routineId)
            userRepository.setUser(User(user.userId, routineId, days[0].dayId))
        }
    }

    fun resetRoutine() {
        val routine = routine.value ?: return
        setUser(routine.routineId)
    }

    fun getUser() {
        viewModelScope.launch {
            val user = userRepository.getUser()
            _userInfo.postValue(user)
            getRoutineById()
        }
    }

    private fun getRoutineById() {
        viewModelScope.launch {
            val routineId = userRepository.getUser().routineId ?: return@launch

            val routine = routineRepository.getRoutineById(routineId)
            _routine.postValue(routine)
        }
    }
}