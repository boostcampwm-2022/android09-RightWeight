package com.lateinit.rightweight.ui.routine.management

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lateinit.rightweight.data.database.entity.Routine
import com.lateinit.rightweight.data.repository.RoutineRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RoutineManagementViewModel @Inject constructor(
    private val routineRepository: RoutineRepository
) : ViewModel() {

    private val _routines = MutableLiveData(listOf<Routine>())
    val routines: LiveData<List<Routine>> get() = _routines

    fun getRoutineList() {
        viewModelScope.launch {
            val routines = routineRepository.getRoutines()
            _routines.postValue(routines)
        }
    }

    fun updateRoutines(routines: List<Routine>) {
        viewModelScope.launch {
            routineRepository.updateRoutines(routines)
        }
    }
}