package com.lateinit.rightweight.ui.routine.management

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lateinit.rightweight.data.repository.RoutineRepository
import com.lateinit.rightweight.data.repository.UserRepository
import com.lateinit.rightweight.ui.model.RoutineUiModel
import com.lateinit.rightweight.util.toRoutineUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RoutineManagementViewModel @Inject constructor(
    userRepository: UserRepository,
    private val routineRepository: RoutineRepository
) : ViewModel() {

    val userInfo =
        userRepository.getUser().stateIn(viewModelScope, SharingStarted.Eagerly, null)

    private val _selectedRoutineUiModel = MutableStateFlow<RoutineUiModel?>(null)
    val selectedRoutineUiModel: StateFlow<RoutineUiModel?> = _selectedRoutineUiModel.asStateFlow()

    private val _routineUiModels = MutableStateFlow<List<RoutineUiModel>>(emptyList())
    val routineUiModels: StateFlow<List<RoutineUiModel>> = _routineUiModels.asStateFlow()

    init {
        viewModelScope.launch {
            userInfo.combine(routineRepository.getRoutines()) { user, routines ->
                user?.routineId to routines.map { it.toRoutineUiModel() }
            }.collect { (selectedRoutineId, routineUiModels) ->
                separateSelectedRoutine(selectedRoutineId, routineUiModels)
            }
        }
    }

    fun moveUpRoutine(routinePosition: Int) {
        if (routinePosition == FIRST_ROUTINE_POSITION) return

        val prevPosition = routinePosition.dec()
        _routineUiModels.value = _routineUiModels.value.swapped(routinePosition, prevPosition)
    }

    fun moveDownRoutine(routinePosition: Int) {
        if (routinePosition == _routineUiModels.value.lastIndex) return

        val nextPosition = routinePosition.inc()
        _routineUiModels.value = _routineUiModels.value.swapped(routinePosition, nextPosition)
    }

    fun updateRoutines() {
        viewModelScope.launch {
            val routines = _routineUiModels.value.toMutableList()
            val selectedRoutine = selectedRoutineUiModel.value ?: return@launch

            routines.add(FIRST_ROUTINE_POSITION, selectedRoutine)
            routineRepository.updateRoutines(routines)
        }
    }

    private fun separateSelectedRoutine(
        selectedRoutineId: String?,
        routineUiModels: List<RoutineUiModel>
    ) {
        _selectedRoutineUiModel.value =
            routineUiModels.find { it.routineId == selectedRoutineId }?.copy(order = 0)
        _routineUiModels.value = routineUiModels
            .filterNot { it.routineId == selectedRoutineId }
            .mapIndexed { index, routine -> routine.copy(order = index + 1) }
    }

    private fun List<RoutineUiModel>.swapped(first: Int, second: Int): List<RoutineUiModel> {
        val result = this.toMutableList()

        result[first] = result[second].copy(order = first.inc()).also {
            result[second] = result[first].copy(order = second.inc())
        }
        return result
    }

    companion object {
        private const val FIRST_ROUTINE_POSITION = 0
    }
}