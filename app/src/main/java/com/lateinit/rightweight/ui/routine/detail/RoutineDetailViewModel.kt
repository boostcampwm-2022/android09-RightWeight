package com.lateinit.rightweight.ui.routine.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lateinit.rightweight.data.database.entity.Routine
import com.lateinit.rightweight.data.repository.RoutineRepository
import com.lateinit.rightweight.data.repository.SharedRoutineRepository
import com.lateinit.rightweight.data.repository.UserRepository
import com.lateinit.rightweight.ui.model.DayUiModel
import com.lateinit.rightweight.ui.model.ExerciseSetUiModel
import com.lateinit.rightweight.ui.model.ExerciseUiModel
import com.lateinit.rightweight.util.FIRST_DAY_POSITION
import com.lateinit.rightweight.util.toDayField
import com.lateinit.rightweight.util.toDayUiModel
import com.lateinit.rightweight.util.toExerciseField
import com.lateinit.rightweight.util.toExerciseSetField
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RoutineDetailViewModel @Inject constructor(
    private val routineRepository: RoutineRepository,
    private val sharedRoutineRepository: SharedRoutineRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    val userInfo = userRepository.getUser().stateIn(viewModelScope, SharingStarted.Eagerly, null)

    private val _routine = MutableLiveData<Routine>()
    val routine: LiveData<Routine> = _routine

    private val _dayUiModels = MutableLiveData<List<DayUiModel>>()
    val dayUiModels: LiveData<List<DayUiModel>> = _dayUiModels

    private val _currentDayPosition = MutableLiveData<Int>()
    val currentDayPosition: LiveData<Int> = _currentDayPosition

    private val _isSelected = MutableSharedFlow<Boolean>()
    val isSelected = _isSelected.asSharedFlow()

    fun selectRoutine() {
        viewModelScope.launch {
            val user = userInfo.value ?: return@launch

            userRepository.saveUser(
                user.copy(
                    routineId = _routine.value?.routineId,
                    dayId = _dayUiModels.value?.first()?.dayId
                )
            )
            _isSelected.emit(true)
        }
    }

    fun deselectRoutine() {
        viewModelScope.launch {
            val user = userInfo.value ?: return@launch

            userRepository.saveUser(
                user.copy(
                    routineId = null,
                    dayId = null
                )
            )
        }
    }

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
    }

    fun removeRoutine(routineId: String) {
        viewModelScope.launch {
            routineRepository.removeRoutineById(routineId)
        }
    }

    fun shareRoutine() {
        val userId = userInfo.value?.userId ?: return
        val nowRoutine = _routine.value ?: return
        val days = _dayUiModels.value ?: return
        viewModelScope.launch {
            sharedRoutineRepository.shareRoutine(userId, nowRoutine.routineId, nowRoutine)
            saveDays(nowRoutine.routineId, days)
        }
    }

    private fun saveDays(routineId: String, dayUiModels: List<DayUiModel>) {
        viewModelScope.launch {
            dayUiModels.forEach { dayUiModel ->
                sharedRoutineRepository.shareDay(
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
                sharedRoutineRepository.shareExercise(
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
                sharedRoutineRepository.shareExerciseSet(
                    routineId,
                    dayId,
                    exerciseId,
                    exerciseSetUiModel.setId,
                    exerciseSetUiModel.toExerciseSetField()
                )
            }
        }
    }

    fun deleteSharedRoutineAndDays() {
        viewModelScope.launch {
            val routineId = _routine.value?.routineId ?: return@launch
            sharedRoutineRepository.deleteDocument(routineId)
            val path = "${routineId}/day"
            val dayDocuments = sharedRoutineRepository.getChildrenDocumentName(path)
            dayDocuments.forEach { dayId ->
                deleteSharedExercise(routineId, dayId)
                sharedRoutineRepository.deleteDocument("${routineId}/day/${dayId}")
            }
        }
    }

    private fun deleteSharedExercise(routineId: String, dayId: String) {
        viewModelScope.launch {
            val path = "${routineId}/day/${dayId}/exercise"
            val exerciseDocuments = sharedRoutineRepository.getChildrenDocumentName(path)
            exerciseDocuments.forEach { exerciseId ->
                deleteSharedExerciseSet(routineId, dayId, exerciseId)
                sharedRoutineRepository.deleteDocument("${routineId}/day/${dayId}/exercise/${exerciseId}")
            }
        }
    }

    private fun deleteSharedExerciseSet(routineId: String, dayId: String, exerciseId: String) {
        viewModelScope.launch {
            val path = "${routineId}/day/${dayId}/exercise/${exerciseId}/exercise_set"
            val exerciseSetDocuments = sharedRoutineRepository.getChildrenDocumentName(path)
            exerciseSetDocuments.forEach { exerciseSetId ->
                sharedRoutineRepository.deleteDocument("${routineId}/day/${dayId}/exercise/${exerciseId}/exercise_set/${exerciseSetId}")
            }
        }
    }

}

