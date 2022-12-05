package com.lateinit.rightweight.ui.routine.management

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.lateinit.rightweight.data.database.entity.Routine
import com.lateinit.rightweight.data.repository.RoutineRepository
import com.lateinit.rightweight.ui.UserViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RoutineManagementViewModel @Inject constructor(
    private val routineRepository: RoutineRepository
) : UserViewModel() {

    private val _routines = MutableLiveData(listOf<Routine>())
    val routines: LiveData<List<Routine>> get() = _routines

    val selectedRoutine
        get() = routineRepository.getSelectedRoutine(userInfo.value?.routineId)
            .stateIn(viewModelScope, SharingStarted.Lazily, null)

    fun getRoutineList() {
        viewModelScope.launch {
            val routines = routineRepository.getRoutines()
            _routines.value = routines
        }
    }

    fun updateRoutines(routines: List<Routine>) {
        viewModelScope.launch {
            routineRepository.updateRoutines(routines)
        }
    }

}