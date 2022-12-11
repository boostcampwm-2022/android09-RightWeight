package com.lateinit.rightweight.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lateinit.rightweight.data.database.intermediate.RoutineWithDays
import com.lateinit.rightweight.data.mapper.remote.toDayField
import com.lateinit.rightweight.data.mapper.remote.toExerciseField
import com.lateinit.rightweight.data.mapper.remote.toExerciseSetField
import com.lateinit.rightweight.data.mapper.remote.toHistoryExerciseField
import com.lateinit.rightweight.data.mapper.remote.toHistoryExerciseSetField
import com.lateinit.rightweight.data.mapper.remote.toHistoryField
import com.lateinit.rightweight.data.mapper.remote.toRoutineField
import com.lateinit.rightweight.data.model.remote.UpdateData
import com.lateinit.rightweight.data.model.remote.WriteModelData
import com.lateinit.rightweight.data.repository.HistoryRepository
import com.lateinit.rightweight.data.repository.LoginRepository
import com.lateinit.rightweight.data.repository.RoutineRepository
import com.lateinit.rightweight.data.repository.UserRepository
import com.lateinit.rightweight.ui.login.NetworkState
import com.lateinit.rightweight.ui.mapper.toDayUiModel
import com.lateinit.rightweight.ui.mapper.toRoutineUiModel
import com.lateinit.rightweight.ui.model.history.HistoryExerciseSetUiModel
import com.lateinit.rightweight.ui.model.history.HistoryExerciseUiModel
import com.lateinit.rightweight.ui.model.history.HistoryUiModel
import com.lateinit.rightweight.ui.model.routine.DayUiModel
import com.lateinit.rightweight.ui.model.routine.ExerciseSetUiModel
import com.lateinit.rightweight.ui.model.routine.ExerciseUiModel
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
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val routineRepository: RoutineRepository,
    private val historyRepository: HistoryRepository,
    private val loginRepository: LoginRepository
) : ViewModel() {

    val userInfo = userRepository.getUser().stateIn(viewModelScope, SharingStarted.Eagerly, null)

    private val _networkState = MutableSharedFlow<NetworkState>()
    val networkState = _networkState.asSharedFlow()

    private val _deleteEvent = MutableSharedFlow<Boolean>()
    val deleteEvent = _deleteEvent.asSharedFlow()

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

    fun deleteLocalData() {
        viewModelScope.launch {
            userRepository.removeUserInfo()
            historyRepository.removeAllHistories()
            routineRepository.removeAllRoutines()
            _deleteEvent.emit(true)
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

    fun restore(){
        val userId = userInfo.value?.userId ?: return
        viewModelScope.launch(networkExceptionHandler) {
            val userInfoInServer = userRepository.restoreUserInfo(userId)
            if(userInfoInServer != null){
                restoreRoutine()
                restoreHistory(userId)
                restoreUserInfo(
                    userInfoInServer.routineId.value,
                    userInfoInServer.dayId.value
                )
            }
        }
    }

    private suspend fun restoreHistory(userId: String) {
        historyRepository.restoreHistory(userId)
    }

    private suspend fun restoreRoutine() {
        val userId = userInfo.value?.userId ?: return
        val routineIds = routineRepository.getUserRoutineIds(userId)
        routineRepository.restoreMyRoutine(routineIds)
    }

    private suspend fun restoreUserInfo(routineId: String, datId: String) {
        val nowUser = userInfo.value ?: return
        userRepository.saveUser(
            nowUser.copy(
                routineId = routineId,
                dayId = datId
            )
        )
    }

    private suspend fun backupUserInfo() {
        val user = userInfo.value ?: return
        userRepository.backupUserInfo(user)
    }

    private suspend fun backupMyRoutine() {
        val userId = userInfo.value?.userId ?: return

        commitItems.clear()
        val myRoutineInServer = routineRepository.getUserRoutineIds(userId)
        myRoutineInServer.forEach { routineId ->
            deleteRoutine(routineId)
        }

        val routineWithDaysList = routineRepository.getAllRoutineWithDays()
        routineWithDaysList.forEach { routineWithDays ->
            updateRoutine(routineWithDays)
        }
        userRepository.commitTransaction(commitItems)
    }

    private suspend fun backupHistory() {
        val userId = userInfo.value?.userId ?: return
        commitItems.clear()
        val lastDate = historyRepository.getLatestHistoryDate(userId)
        val historyList = historyRepository.getHistoryAfterDate(lastDate)
        if (historyList.isNotEmpty()) {
            historyList.forEach { history ->
                updateHistory(history)
            }
            userRepository.commitTransaction(commitItems)
        }
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
        updateDays(path, routineWithDays.days.map { it.toDayUiModel() })
    }

    private fun updateDays(
        lastPath: String,
        days: List<DayUiModel>
    ) {
        days.forEach { dayUiModel ->
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
        val dayIds = routineRepository.getChildrenDocumentName("routine/${routineId}/day")
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
            val exerciseIds = routineRepository.getChildrenDocumentName("routine/$path/exercise")
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
                routineRepository.getChildrenDocumentName("routine/$path/exercise_set")
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

}