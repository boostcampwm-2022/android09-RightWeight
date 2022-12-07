package com.lateinit.rightweight.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lateinit.rightweight.data.database.intermediate.DayWithExercises
import com.lateinit.rightweight.data.database.intermediate.RoutineWithDays
import com.lateinit.rightweight.data.model.UpdateData
import com.lateinit.rightweight.data.model.WriteModelData
import com.lateinit.rightweight.data.repository.UserRepository
import com.lateinit.rightweight.ui.model.ExerciseSetUiModel
import com.lateinit.rightweight.ui.model.ExerciseUiModel
import com.lateinit.rightweight.util.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    val userRepository: UserRepository
) : ViewModel() {

    val userInfo = userRepository.getUser().stateIn(viewModelScope, SharingStarted.Eagerly, null)

    private val commitItems = mutableListOf<WriteModelData>()

    fun backupUserInfo() {
        val user = userInfo.value ?: return
        viewModelScope.launch {
            userRepository.backupUserInfo(user)
        }
    }

    fun backupMyRoutine(){
        commitItems.clear()
        viewModelScope.launch {
            val routineWithDaysList = userRepository.getAllRoutineWithDays()

            routineWithDaysList.forEach { routineWithDays ->
                updateRoutine(routineWithDays)
            }
        }
    }

    private suspend fun updateRoutine(routineWithDays: RoutineWithDays) {
        val routine = routineWithDays.routine.toRoutineUiModel()
        val days = routineWithDays.days
        val path = "${WriteModelData.defaultPath}/routine/${routine.routineId}"
        commitItems.add(
            WriteModelData(
                update = UpdateData(path, routine.toSharedRoutineField(routine.routineId))
            )
        )
        updateDays(path, days)
        userRepository.commitTransaction(commitItems)
    }


    private fun updateDays(
        lastPath: String,
        days: List<DayWithExercises>
    ) {
        days.forEach { day ->
            val dayUiModel = day.toDayUiModel()
            val path = "${lastPath}/day/${dayUiModel.dayId}"
            commitItems.add(
                WriteModelData(
                    update = UpdateData(path, dayUiModel.toDayField())
                )
            )
            updateExercises(path, dayUiModel.exercises)
        }
    }

    private fun updateExercises(
        lastPath: String,
        exerciseUiModels: List<ExerciseUiModel>
    ) {
        exerciseUiModels.forEach { exerciseUiModel ->
            val path = "${lastPath}/exercise/${exerciseUiModel.exerciseId}"
            commitItems.add(
                WriteModelData(
                    update = UpdateData(path, exerciseUiModel.toExerciseField())
                )
            )
            updateExerciseSets(path, exerciseUiModel.exerciseSets)
        }
    }

    private fun updateExerciseSets(
        lastPath: String,
        exerciseSets: List<ExerciseSetUiModel>
    ) {
        exerciseSets.forEach { exerciseSetUiModel ->
            val path = "${lastPath}/exercise_set/${exerciseSetUiModel.setId} "
            commitItems.add(
                WriteModelData(
                    update = UpdateData(path, exerciseSetUiModel.toExerciseSetField())
                )
            )
        }
    }
}