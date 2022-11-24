package com.lateinit.rightweight.ui.routine.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lateinit.rightweight.data.database.entity.Routine
import com.lateinit.rightweight.data.repository.RoutineRepository
import com.lateinit.rightweight.ui.model.DayUiModel
import com.lateinit.rightweight.util.FIRST_DAY_POSITION
import com.lateinit.rightweight.util.toDayUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RoutineDetailViewModel @Inject constructor(
    private val routineRepository: RoutineRepository
) : ViewModel() {

    private val _routine = MutableLiveData<Routine>()
    val routine: LiveData<Routine> = _routine

    private val _dayUiModels = MutableLiveData<List<DayUiModel>>()
    val dayUiModels: LiveData<List<DayUiModel>> = _dayUiModels

    private val _currentDayPosition = MutableLiveData<Int>()
    val currentDayPosition: LiveData<Int> = _currentDayPosition

    fun getRoutine(routineId: String) {
        viewModelScope.launch {
            val routineWithDays = routineRepository.getRoutineWithDaysByRoutineId(routineId)
            _routine.value = routineWithDays.routine
            _dayUiModels.value = routineWithDays.days.mapIndexed { index, routineWithDay ->
                routineWithDay.day.toDayUiModel(index, routineWithDay.exercises)
            }
            _currentDayPosition.value = FIRST_DAY_POSITION

        }
    }

    fun clickDay(dayPosition: Int) {
        if (_currentDayPosition.value == dayPosition) return

        val originDayUiModels = _dayUiModels.value?.toMutableList() ?: return
        val lastSelectedPosition = _currentDayPosition.value ?: return

        originDayUiModels[lastSelectedPosition] = originDayUiModels[lastSelectedPosition].copy(selected = false)
        originDayUiModels[dayPosition] = originDayUiModels[dayPosition].copy(selected = true)

        _currentDayPosition.value = dayPosition
        _dayUiModels.value = originDayUiModels
    }
}

