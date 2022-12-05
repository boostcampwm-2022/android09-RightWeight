package com.lateinit.rightweight.ui.routine.management

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.lateinit.rightweight.data.database.entity.Routine
import com.lateinit.rightweight.data.repository.RoutineRepository
import com.lateinit.rightweight.ui.UserViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RoutineManagementViewModel @Inject constructor(
    private val routineRepository: RoutineRepository
) : UserViewModel() {

    private val _routines = MutableLiveData(listOf<Routine>())
    val routines: LiveData<List<Routine>> get() = _routines

    private val _selectedRoutine = MutableLiveData<Routine>()
    val selectedRoutine: LiveData<Routine> get() = _selectedRoutine

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

    fun loadSelectedRoutine(routineId: String?) {
        routineId ?: return
        viewModelScope.launch {
            val routine = routineRepository.getRoutineById(routineId)
            _selectedRoutine.value = routine
        }
    }
}