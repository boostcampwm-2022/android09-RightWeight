package com.lateinit.rightweight.ui.routine.detail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lateinit.rightweight.data.database.entity.Routine
import com.lateinit.rightweight.data.model.CommitType
import com.lateinit.rightweight.data.model.UpdateData
import com.lateinit.rightweight.data.model.WriteModelData
import com.lateinit.rightweight.data.remote.model.RemoteData
import com.lateinit.rightweight.data.repository.RoutineRepository
import com.lateinit.rightweight.data.repository.SharedRoutineRepository
import com.lateinit.rightweight.ui.model.DayUiModel
import com.lateinit.rightweight.ui.model.ExerciseSetUiModel
import com.lateinit.rightweight.ui.model.ExerciseUiModel
import com.lateinit.rightweight.util.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RoutineDetailViewModel @Inject constructor(
    private val routineRepository: RoutineRepository,
    private val sharedRoutineRepository: SharedRoutineRepository
) : ViewModel() {

    private val _routine = MutableLiveData<Routine>()
    val routine: LiveData<Routine> = _routine

    private val _dayUiModels = MutableLiveData<List<DayUiModel>>()
    val dayUiModels: LiveData<List<DayUiModel>> = _dayUiModels

    private val _currentDayPosition = MutableLiveData<Int>()
    val currentDayPosition: LiveData<Int> = _currentDayPosition

    private var commitItems = mutableListOf<WriteModelData>()

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

    fun commitRoutine(userId: String, commitType: CommitType) {
        commitItems.clear()
        val nowRoutine = _routine.value ?: return
        val days = _dayUiModels.value ?: return
        val path = "${UpdateData.defaultPath}/shared_routine/${nowRoutine.routineId}"
        addCommitData(path, nowRoutine.toSharedRoutineField(userId), commitType)
        commitDays(path, days, commitType)
    }

    private fun commitDays(
        lastPath: String,
        dayUiModels: List<DayUiModel>,
        commitType: CommitType
    ) {
        dayUiModels.forEach { dayUiModel ->
            val path = "${lastPath}/day/${dayUiModel.dayId}"
            addCommitData(path, dayUiModel.toDayField(), commitType)
            commitExercises(path, dayUiModel.exercises, commitType)
        }
    }

    private fun commitExercises(
        lastPath: String,
        exerciseUiModels: List<ExerciseUiModel>,
        commitType: CommitType
    ) {
        exerciseUiModels.forEach { exerciseUiModel ->
            val path = "${lastPath}/exercise/${exerciseUiModel.exerciseId}"
            addCommitData(path, exerciseUiModel.toExerciseField(), commitType)
            commitExerciseSets(path, exerciseUiModel.exerciseSets, commitType)
        }

    }

    private fun commitExerciseSets(
        lastPath: String,
        exerciseSets: List<ExerciseSetUiModel>,
        commitType: CommitType
    ) {
        exerciseSets.forEach { exerciseSetUiModel ->
            val path = "${lastPath}/exercise_set/${exerciseSetUiModel.setId} "
            addCommitData(path, exerciseSetUiModel.toExerciseSetField(), commitType)
        }
        viewModelScope.launch {
            sharedRoutineRepository.commitTransaction(commitItems)
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

    private fun addCommitData(path: String, remoteData: RemoteData, commitType: CommitType) {
        commitItems.add(
            when (commitType) {
                CommitType.UPDATE -> WriteModelData(
                    update = UpdateData(path, remoteData)
                )
                CommitType.DELETE -> WriteModelData(delete = path)
            }
        )
    }
}

