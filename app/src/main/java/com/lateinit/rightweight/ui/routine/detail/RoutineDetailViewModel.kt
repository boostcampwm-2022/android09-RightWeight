package com.lateinit.rightweight.ui.routine.detail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lateinit.rightweight.data.database.entity.Routine
import com.lateinit.rightweight.data.repository.RoutineRepository
import com.lateinit.rightweight.ui.model.DayUiModel
import com.lateinit.rightweight.ui.model.ExerciseSetUiModel
import com.lateinit.rightweight.ui.model.ExerciseUiModel
import com.lateinit.rightweight.util.*
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

            Log.d("detailFragment", "${_routine.value?.routineId}")
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

        originDayUiModels[lastSelectedPosition] =
            originDayUiModels[lastSelectedPosition].copy(selected = false)
        originDayUiModels[dayPosition] = originDayUiModels[dayPosition].copy(selected = true)

        _currentDayPosition.value = dayPosition
        _dayUiModels.value = originDayUiModels
    }

    fun clickExercise(exercisePosition: Int) {
        val nowDayPosition = _currentDayPosition.value ?: return
        val originDayUiModels = _dayUiModels.value?.toMutableList() ?: return
        val originExerciseUiModels = originDayUiModels[nowDayPosition].exercises.toMutableList()
        val exerciseUiModel = originExerciseUiModels[exercisePosition]

        originExerciseUiModels[exercisePosition] = exerciseUiModel.copy(
            expanded = exerciseUiModel.expanded.not()
        )

        originDayUiModels[nowDayPosition] =
            originDayUiModels[nowDayPosition].copy(exercises = originExerciseUiModels)

        _dayUiModels.value = originDayUiModels
        _currentDayPosition.value = _currentDayPosition.value
    }

    fun removeRoutine(routineId: String) {
        viewModelScope.launch {
            routineRepository.removeRoutineById(routineId)
        }
    }

    fun shareRoutine(userId: String) {
        val nowRoutine = _routine.value ?: return
        val days = _dayUiModels.value ?: return
        viewModelScope.launch {
            routineRepository.shareRoutine(userId, nowRoutine.routineId, nowRoutine)
            saveDays(nowRoutine.routineId, days)
        }
    }

    private fun saveDays(routineId: String, dayUiModels: List<DayUiModel>) {
        viewModelScope.launch {
            dayUiModels.forEach { dayUiModel ->
                routineRepository.shareDay(
                    routineId,
                    dayUiModel.dayId,
                    dayUiModel.toDayField()
                )
                saveExercise(routineId, dayUiModel.dayId, dayUiModel.exercises)
            }
        }
    }

    private fun saveExercise(routineId: String, dayId: String, exercises: List<ExerciseUiModel>) {
        viewModelScope.launch {
            exercises.forEach { exerciseUiModel ->
                routineRepository.shareExercise(
                    routineId,
                    dayId,
                    exerciseUiModel.exerciseId,
                    exerciseUiModel.toExerciseField()
                )
                saveExerciseSet(
                    routineId,
                    dayId,
                    exerciseUiModel.exerciseId,
                    exerciseUiModel.exerciseSets
                )
            }
        }

    }

    private fun saveExerciseSet(
        routineId: String,
        dayId: String,
        exerciseId: String,
        exerciseSets: List<ExerciseSetUiModel>
    ) {
        viewModelScope.launch {
            exerciseSets.forEach { exerciseSetUiModel ->
                routineRepository.shareExerciseSet(
                    routineId,
                    dayId,
                    exerciseId,
                    exerciseSetUiModel.setId,
                    exerciseSetUiModel.toExerciseSetField()
                )
            }
        }
    }
}

