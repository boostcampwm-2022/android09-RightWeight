package com.lateinit.rightweight.ui.routine.detail

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
import com.lateinit.rightweight.data.repository.UserRepository
import com.lateinit.rightweight.ui.model.DayUiModel
import com.lateinit.rightweight.ui.model.ExerciseSetUiModel
import com.lateinit.rightweight.ui.model.ExerciseUiModel
import com.lateinit.rightweight.util.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
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

    private var commitItems = mutableListOf<WriteModelData>()

    fun selectRoutine() {
        viewModelScope.launch {
            val user = userInfo.value ?: return@launch

            userRepository.saveUser(
                user.copy(
                    routineId = _routine.value?.routineId,
                    dayId = _dayUiModels.value?.first()?.dayId
                )
            )
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

    fun updateSharedRoutine() {
        commitItems.clear()
        val updateType = CommitType.UPDATE
        val userId = userInfo.value?.userId ?: return
        val nowRoutine = _routine.value ?: return
        val days = _dayUiModels.value ?: return
        val path = "${WriteModelData.defaultPath}/shared_routine/${nowRoutine.routineId}"
        addCommitData(path, nowRoutine.toSharedRoutineField(userId), updateType)
        updateDays(path, days, updateType)
    }

    private fun updateDays(
        lastPath: String,
        dayUiModels: List<DayUiModel>,
        commitType: CommitType
    ) {
        dayUiModels.forEach { dayUiModel ->
            val path = "${lastPath}/day/${dayUiModel.dayId}"
            addCommitData(path, dayUiModel.toDayField(), commitType)
            updateExercises(path, dayUiModel.exercises, commitType)
        }
    }

    private fun updateExercises(
        lastPath: String,
        exerciseUiModels: List<ExerciseUiModel>,
        commitType: CommitType
    ) {
        exerciseUiModels.forEach { exerciseUiModel ->
            val path = "${lastPath}/exercise/${exerciseUiModel.exerciseId}"
            addCommitData(path, exerciseUiModel.toExerciseField(), commitType)
            updateExerciseSets(path, exerciseUiModel.exerciseSets, commitType)
        }
    }

    private fun updateExerciseSets(
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


    fun deleteSharedRoutine() {
        commitItems.clear()
        val routineId = _routine.value?.routineId ?: return
        commitItems.add(
            WriteModelData(delete = "${WriteModelData.defaultPath}/shared_routine/${routineId}")
        )
        viewModelScope.launch {
            val dayIds = sharedRoutineRepository.getChildrenDocumentName("$routineId/day")
            deleteDays(routineId, dayIds)
        }
    }

    private suspend fun deleteDays(
        lastPath: String,
        dayIds: List<String>
    ) {
        dayIds.forEach { dayId ->
            val path = "${lastPath}/day/${dayId}"
            commitItems.add(
                WriteModelData(delete = "${WriteModelData.defaultPath}/shared_routine/${path}")
            )
            val exerciseIds = sharedRoutineRepository.getChildrenDocumentName("$path/exercise")
            deleteExercises(path, exerciseIds)
        }
    }

    private suspend fun deleteExercises(
        lastPath: String,
        exerciseIds: List<String>
    ) {
        exerciseIds.forEach { exerciseId ->
            val path = "${lastPath}/exercise/${exerciseId}"
            commitItems.add(
                WriteModelData(delete = "${WriteModelData.defaultPath}/shared_routine/${path}")
            )
            val exerciseSetIds =
                sharedRoutineRepository.getChildrenDocumentName("$path/exercise_set")
            deleteExerciseSets(path, exerciseSetIds)
        }
    }

    private suspend fun deleteExerciseSets(
        lastPath: String,
        exerciseSetIds: List<String>,
    ) {
        exerciseSetIds.forEach { exerciseSetId ->
            val path = "${lastPath}/exercise_set/${exerciseSetId} "
            commitItems.add(
                WriteModelData(delete = "${WriteModelData.defaultPath}/shared_routine/${path}")
            )
        }
        sharedRoutineRepository.commitTransaction(commitItems)
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

