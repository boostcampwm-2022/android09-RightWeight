package com.lateinit.rightweight.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lateinit.rightweight.data.database.intermediate.DayWithExercises
import com.lateinit.rightweight.data.database.intermediate.RoutineWithDays
import com.lateinit.rightweight.data.model.UpdateData
import com.lateinit.rightweight.data.model.WriteModelData
import com.lateinit.rightweight.data.repository.LoginRepository
import com.lateinit.rightweight.data.repository.UserRepository
import com.lateinit.rightweight.ui.login.NetworkState
import com.lateinit.rightweight.ui.model.ExerciseSetUiModel
import com.lateinit.rightweight.ui.model.ExerciseUiModel
import com.lateinit.rightweight.util.toDayField
import com.lateinit.rightweight.util.toDayUiModel
import com.lateinit.rightweight.util.toExerciseField
import com.lateinit.rightweight.util.toExerciseSetField
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
        viewModelScope.launch(networkExceptionHandler) {
            backupUserInfo()
            backupMyRoutine()
            sendNetworkResultEvent(NetworkState.SUCCESS)
        }
    }

    private suspend fun backupUserInfo() {
        val user = userInfo.value ?: return
        userRepository.backupUserInfo(user)
        getUserRoutineInRemote()
    }

    private suspend fun backupMyRoutine() {
        commitItems.clear()
        val routineWithDaysList = userRepository.getAllRoutineWithDays()
        routineWithDaysList.forEach { routineWithDays ->
            updateRoutine(routineWithDays)
        }
        userRepository.commitTransaction(commitItems)
    }

    private fun updateRoutine(routineWithDays: RoutineWithDays) {
        val userId = userInfo.value?.userId ?: return
        val routine = routineWithDays.routine.toRoutineUiModel()
        val days = routineWithDays.days
        val path = "${WriteModelData.defaultPath}/routine/${routine.routineId}"
        commitItems.add(
            WriteModelData(
                update = UpdateData(path, routine.toRoutineField(userId))
            )
        )
        updateDays(path, days)
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

    private fun getUserRoutineInRemote() {
        val userId = userInfo.value?.userId ?: return
        viewModelScope.launch {
            userRepository.getUserRoutineInRemote(userId)
        }
    }
}