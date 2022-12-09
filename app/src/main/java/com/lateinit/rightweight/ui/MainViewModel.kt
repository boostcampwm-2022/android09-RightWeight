package com.lateinit.rightweight.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lateinit.rightweight.data.database.intermediate.DayWithExercises
import com.lateinit.rightweight.data.database.intermediate.RoutineWithDays
import com.lateinit.rightweight.data.model.remote.UpdateData
import com.lateinit.rightweight.data.model.remote.WriteModelData
import com.lateinit.rightweight.data.repository.LoginRepository
import com.lateinit.rightweight.data.repository.UserRepository
import com.lateinit.rightweight.ui.login.NetworkState
import com.lateinit.rightweight.ui.model.ExerciseSetUiModel
import com.lateinit.rightweight.ui.model.ExerciseUiModel
import com.lateinit.rightweight.ui.model.HistoryExerciseSetUiModel
import com.lateinit.rightweight.ui.model.HistoryExerciseUiModel
import com.lateinit.rightweight.ui.model.HistoryUiModel
import com.lateinit.rightweight.util.toDayField
import com.lateinit.rightweight.util.toDayUiModel
import com.lateinit.rightweight.util.toExerciseField
import com.lateinit.rightweight.util.toExerciseSetField
import com.lateinit.rightweight.util.toHistoryExerciseField
import com.lateinit.rightweight.util.toHistoryExerciseSetField
import com.lateinit.rightweight.util.toHistoryField
import com.lateinit.rightweight.util.toRoutineField
import com.lateinit.rightweight.util.toRoutineUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.net.SocketException
import java.net.UnknownHostException
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val loginRepository: LoginRepository
) : ViewModel() {

    val userInfo = userRepository.getUser().stateIn(viewModelScope, SharingStarted.Eagerly, null)

    private val _networkState = MutableSharedFlow<NetworkState>()
    val networkState = _networkState.asSharedFlow()

    private val commitItems = mutableListOf<WriteModelData>()

    private val networkExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        viewModelScope.launch {
            when (throwable) {
                is SocketException -> sendNetworkResultEvent(NetworkState.BAD_INTERNET)
                is HttpException -> sendNetworkResultEvent(NetworkState.PARSE_ERROR)
                is UnknownHostException -> sendNetworkResultEvent(NetworkState.WRONG_CONNECTION)
                else -> sendNetworkResultEvent(NetworkState.OTHER_ERROR)
            }
        }
    }

    fun deleteAccount(key: String) {
        val userInfo = userInfo.value?.idToken ?: return
        viewModelScope.launch(networkExceptionHandler) {
            loginRepository.deleteAccount(key, userInfo)
            sendNetworkResultEvent(NetworkState.SUCCESS)
        }
    }

    private suspend fun sendNetworkResultEvent(state: NetworkState) {
        _networkState.emit(state)
    }

    fun backup() {
        viewModelScope.launch() {
            backupUserInfo()
            backupMyRoutine()
            backupHistory()
            sendNetworkResultEvent(NetworkState.SUCCESS)
        }
    }

    private suspend fun backupUserInfo() {
        val user = userInfo.value ?: return
        userRepository.backupUserInfo(user)
    }

    private suspend fun backupMyRoutine() {
        val userId = userInfo.value?.userId ?: return

        commitItems.clear()
        val myRoutineInServer = userRepository.getUserRoutineIds(userId)
        myRoutineInServer.forEach { routineId ->
            deleteRoutine(routineId)
        }

        val routineWithDaysList = userRepository.getAllRoutineWithDays()
        routineWithDaysList.forEach { routineWithDays ->
            updateRoutine(routineWithDays)
        }
        userRepository.commitTransaction(commitItems)
    }

    private suspend fun backupHistory() {
        val userId = userInfo.value?.userId ?: return
        commitItems.clear()
        val lastDate = getLatestHistoryDate(userId)
        val historyList = userRepository.getHistoryAfterDate(lastDate)
        if (historyList.isNotEmpty()) {
            historyList.forEach { history ->
                updateHistory(history)
            }
            userRepository.commitTransaction(commitItems)
        }
    }

    private suspend fun getLatestHistoryDate(userId: String): LocalDate {
        return userRepository.getLatestHistoryDate(userId) ?: return DEFAULT_LOCAL_DATE
    }

    private fun updateHistory(history: HistoryUiModel) {
        val userId = userInfo.value?.userId ?: return
        val path = "${WriteModelData.defaultPath}/user/${userId}/history/${history.historyId}"
        commitItems.add(
            WriteModelData(
                update = UpdateData(path, history.toHistoryField())
            )
        )
        updateHistoryExercises(path, history.exercises)
    }

    private fun updateHistoryExercises(lastPath: String, exercises: List<HistoryExerciseUiModel>) {
        exercises.forEach { exercise ->
            val path = "${lastPath}/exercise/${exercise.exerciseId}"
            commitItems.add(
                WriteModelData(
                    update = UpdateData(path, exercise.toHistoryExerciseField())
                )
            )
            updateHistoryExerciseSets(path, exercise.exerciseSets)
        }
    }

    private fun updateHistoryExerciseSets(
        lastPath: String,
        exerciseSets: List<HistoryExerciseSetUiModel>
    ) {
        exerciseSets.forEach { exerciseSet ->
            val path = "${lastPath}/exercise_set/${exerciseSet.setId}"
            commitItems.add(
                WriteModelData(
                    update = UpdateData(path, exerciseSet.toHistoryExerciseSetField())
                )
            )
        }
    }

    private fun updateRoutine(routineWithDays: RoutineWithDays) {
        val userId = userInfo.value?.userId ?: return
        val routine = routineWithDays.routine.toRoutineUiModel()
        val path = "${WriteModelData.defaultPath}/routine/${routine.routineId}"
        commitItems.add(
            WriteModelData(
                update = UpdateData(path, routine.toRoutineField(userId))
            )
        )
        updateDays(path, routineWithDays.days)
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
            val path = "${lastPath}/exercise_set/${exerciseSetUiModel.setId}"
            commitItems.add(
                WriteModelData(
                    update = UpdateData(path, exerciseSetUiModel.toExerciseSetField())
                )
            )
        }
    }

    private suspend fun deleteRoutine(routineId: String) {
        commitItems.add(
            WriteModelData(delete = "${WriteModelData.defaultPath}/routine/${routineId}")
        )
        val dayIds = userRepository.getChildrenDocumentName("$routineId/day")
        deleteDays(routineId, dayIds)
    }


    private suspend fun deleteDays(
        lastPath: String,
        dayIds: List<String>
    ) {
        dayIds.forEach { dayId ->
            val path = "${lastPath}/day/${dayId}"
            commitItems.add(
                WriteModelData(delete = "${WriteModelData.defaultPath}/routine/${path}")
            )
            val exerciseIds = userRepository.getChildrenDocumentName("$path/exercise")
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
                WriteModelData(delete = "${WriteModelData.defaultPath}/routine/${path}")
            )
            val exerciseSetIds =
                userRepository.getChildrenDocumentName("$path/exercise_set")
            deleteExerciseSets(path, exerciseSetIds)
        }
    }

    private fun deleteExerciseSets(
        lastPath: String,
        exerciseSetIds: List<String>,
    ) {
        exerciseSetIds.forEach { exerciseSetId ->
            val path = "${lastPath}/exercise_set/${exerciseSetId}"
            commitItems.add(
                WriteModelData(delete = "${WriteModelData.defaultPath}/routine/${path}")
            )
        }
    }

    companion object {
        val DEFAULT_LOCAL_DATE: LocalDate = LocalDate.parse("1990-01-01")
    }
}