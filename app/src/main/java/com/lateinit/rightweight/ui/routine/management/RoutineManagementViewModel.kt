package com.lateinit.rightweight.ui.routine.management

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lateinit.rightweight.data.repository.RoutineRepository
import com.lateinit.rightweight.data.repository.UserRepository
import com.lateinit.rightweight.ui.model.RoutineUiModel
import com.lateinit.rightweight.util.toRoutineUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RoutineManagementViewModel @Inject constructor(
    private val routineRepository: RoutineRepository,
    userRepository: UserRepository
) : ViewModel() {

    private val userInfo =
        userRepository.getUser().stateIn(viewModelScope, SharingStarted.Eagerly, null)

    val selectedRoutine = userInfo.map {
        it?.routineId ?: return@map null
        routineRepository.getRoutineById(it.routineId).toRoutineUiModel()
    }.stateIn(viewModelScope, SharingStarted.Lazily, null)

    private val _routineUiModels = MutableLiveData<List<RoutineUiModel>>(emptyList())
    val routineUiModels: LiveData<List<RoutineUiModel>> get() = _routineUiModels

    fun getRoutineList() {
        viewModelScope.launch {
            val routines = routineRepository.getRoutines().map { it.toRoutineUiModel() }
            _routineUiModels.value = routines
        }
    }

    fun updateRoutines(routines: List<RoutineUiModel>) {
        viewModelScope.launch {
            routineRepository.updateRoutines(routines)
        }
    }
}