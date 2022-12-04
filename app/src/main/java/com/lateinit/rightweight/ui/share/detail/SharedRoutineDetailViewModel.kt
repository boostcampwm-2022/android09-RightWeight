package com.lateinit.rightweight.ui.share.detail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lateinit.rightweight.data.database.entity.SharedRoutineDay
import com.lateinit.rightweight.data.database.entity.SharedRoutineExercise
import com.lateinit.rightweight.data.database.entity.SharedRoutineExerciseSet
import com.lateinit.rightweight.data.repository.SharedRoutineRepository
import com.lateinit.rightweight.ui.model.DayUiModel
import com.lateinit.rightweight.util.FIRST_DAY_POSITION
import com.lateinit.rightweight.util.toDayUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SharedRoutineDetailViewModel @Inject constructor(
    private val sharedRoutineRepository: SharedRoutineRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        LatestSharedRoutineDetailUiState.Success(mutableListOf())
    )
    val uiState: StateFlow<LatestSharedRoutineDetailUiState> = _uiState

    private val _currentDayPosition = MutableLiveData<Int>()
    val currentDayPosition: LiveData<Int> = _currentDayPosition

    fun getSharedRoutineDetail(routineId: String) {
        viewModelScope.launch {
            sharedRoutineRepository.getSharedRoutineDetail(routineId).collect(){ sharedRoutineWithDays ->
                _uiState.value = LatestSharedRoutineDetailUiState.Success(sharedRoutineWithDays.days.mapIndexed { index, sharedRoutineWithDay ->
                    sharedRoutineWithDay.day.toDayUiModel(index, sharedRoutineWithDay.exercises)
                })
                _currentDayPosition.value = FIRST_DAY_POSITION
            }

        }
    }

    fun clickDay(dayPosition: Int) {
        if (_currentDayPosition.value == dayPosition) return

        val originDayUiModels = _uiState.value.dayUiModels.toMutableList()
        val lastSelectedPosition = _currentDayPosition.value ?: return

        originDayUiModels[lastSelectedPosition] =
            originDayUiModels[lastSelectedPosition].copy(selected = false)
        originDayUiModels[dayPosition] = originDayUiModels[dayPosition].copy(selected = true)

        _currentDayPosition.value = dayPosition
        _uiState.value = LatestSharedRoutineDetailUiState.Success(originDayUiModels)
    }

    fun clickExercise(exercisePosition: Int) {
        val nowDayPosition = _currentDayPosition.value ?: return
        val originDayUiModels = _uiState.value.dayUiModels.toMutableList()
        val originExerciseUiModels = originDayUiModels[nowDayPosition].exercises.toMutableList()
        val exerciseUiModel = originExerciseUiModels[exercisePosition]

        originExerciseUiModels[exercisePosition] = exerciseUiModel.copy(
            expanded = exerciseUiModel.expanded.not()
        )

        originDayUiModels[nowDayPosition] =
            originDayUiModels[nowDayPosition].copy(exercises = originExerciseUiModels)

        _currentDayPosition.value = _currentDayPosition.value
        _uiState.value = LatestSharedRoutineDetailUiState.Success(originDayUiModels)
    }

}

sealed class LatestSharedRoutineDetailUiState {
    data class Success(
        val dayUiModels: List<DayUiModel>
    ) : LatestSharedRoutineDetailUiState()

    data class Error(val exception: Throwable) : LatestSharedRoutineDetailUiState()
}

