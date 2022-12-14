package com.lateinit.rightweight.ui.routine.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lateinit.rightweight.data.mapper.remote.toDayField
import com.lateinit.rightweight.data.mapper.remote.toExerciseField
import com.lateinit.rightweight.data.mapper.remote.toExerciseSetField
import com.lateinit.rightweight.data.mapper.remote.toSharedRoutineField
import com.lateinit.rightweight.data.model.remote.UpdateData
import com.lateinit.rightweight.data.model.remote.WriteModelData
import com.lateinit.rightweight.data.repository.RoutineRepository
import com.lateinit.rightweight.data.repository.SharedRoutineRepository
import com.lateinit.rightweight.data.repository.UserRepository
import com.lateinit.rightweight.ui.login.NetworkState
import com.lateinit.rightweight.ui.mapper.toDayUiModel
import com.lateinit.rightweight.ui.mapper.toRoutineUiModel
import com.lateinit.rightweight.ui.model.routine.DayUiModel
import com.lateinit.rightweight.ui.model.routine.ExerciseSetUiModel
import com.lateinit.rightweight.ui.model.routine.ExerciseUiModel
import com.lateinit.rightweight.ui.model.routine.RoutineUiModel
import com.lateinit.rightweight.util.DEFAULT_ROUTINE_ID
import com.lateinit.rightweight.util.FIRST_DAY_POSITION
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
class RoutineDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val routineRepository: RoutineRepository,
    private val sharedRoutineRepository: SharedRoutineRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    val routineId =
        savedStateHandle.get<String>("routineId") ?: DEFAULT_ROUTINE_ID

    val userInfo = userRepository.getUser().stateIn(viewModelScope, SharingStarted.Eagerly, null)

    private val _routineUiModel = MutableLiveData<RoutineUiModel>()
    val routineUiModel: LiveData<RoutineUiModel> = _routineUiModel

    private val _dayUiModels = MutableLiveData<List<DayUiModel>>()
    val dayUiModels: LiveData<List<DayUiModel>> = _dayUiModels

    private val _currentDayPosition = MutableLiveData<Int>()
    val currentDayPosition: LiveData<Int> = _currentDayPosition

    private val commitItems = mutableListOf<WriteModelData>()

    private val _navigationEvent = MutableSharedFlow<NavigationEvent>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    private val _networkState = MutableSharedFlow<NetworkState>()
    val networkState = _networkState.asSharedFlow()

    init {
        getRoutine()
    }

    fun selectRoutine() {
        viewModelScope.launch {
            val user = userInfo.value ?: return@launch

            userRepository.saveUser(
                user.copy(
                    routineId = _routineUiModel.value?.routineId ?: "",
                    dayId = _dayUiModels.value?.first()?.dayId ?: ""
                )
            )
            sendEvent(NavigationEvent.SelectEvent(true))
        }
    }

    fun deselectRoutine() {
        viewModelScope.launch {
            val user = userInfo.value ?: return@launch

            userRepository.saveUser(
                user.copy(
                    routineId = "",
                    dayId = ""
                )
            )
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

    fun removeRoutine() {
        viewModelScope.launch {
            routineRepository.removeRoutineById(routineId)
            sendEvent(NavigationEvent.RemoveEvent(true))
        }
    }

    fun shareRoutine() {
        val nowRoutine = _routineUiModel.value ?: return
        viewModelScope.launch(networkExceptionHandler) {
            if (sharedRoutineRepository.isRoutineShared(nowRoutine.routineId)) {
                deleteSharedRoutine()
            }
            updateSharedRoutine()
            sendNetworkResultEvent(NetworkState.SUCCESS)
        }
    }

    private fun getRoutine() {
        viewModelScope.launch {
            val routineWithDays = routineRepository.getRoutineWithDaysByRoutineId(routineId)

            _routineUiModel.value = routineWithDays.routine.toRoutineUiModel()
            _dayUiModels.value = routineWithDays.days.mapIndexed { index, dayWithExercise ->
                dayWithExercise.day.toDayUiModel(index, dayWithExercise.exercises)
            }
            _currentDayPosition.value = FIRST_DAY_POSITION
        }
    }

    private suspend fun updateSharedRoutine() {
        commitItems.clear()
        val nowRoutine = _routineUiModel.value ?: return
        val days = _dayUiModels.value ?: return
        val path = "${WriteModelData.defaultPath}/shared_routine/${nowRoutine.routineId}"
        commitItems.add(
            WriteModelData(
                update = UpdateData(path, nowRoutine.toSharedRoutineField(nowRoutine.routineId))
            )
        )
        updateDays(path, days)
        sharedRoutineRepository.commitTransaction(commitItems)
    }

    private fun updateDays(
        lastPath: String,
        dayUiModels: List<DayUiModel>
    ) {
        dayUiModels.forEach { dayUiModel ->
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

    private suspend fun deleteSharedRoutine() {
        commitItems.clear()
        val routineId = _routineUiModel.value?.routineId ?: return
        commitItems.add(
            WriteModelData(delete = "${WriteModelData.defaultPath}/shared_routine/${routineId}")
        )
        val dayIds =
            sharedRoutineRepository.getChildrenDocumentName("shared_routine/$routineId/day")
        deleteDays(routineId, dayIds)
        sharedRoutineRepository.commitTransaction(commitItems)
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
            val exerciseIds =
                sharedRoutineRepository.getChildrenDocumentName("shared_routine/$path/exercise")
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
                sharedRoutineRepository.getChildrenDocumentName("shared_routine/$path/exercise_set")
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
                WriteModelData(delete = "${WriteModelData.defaultPath}/shared_routine/${path}")
            )
        }
    }

    private fun sendEvent(event: NavigationEvent) {
        viewModelScope.launch {
            _navigationEvent.emit(event)
        }
    }

    private val networkExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        when (throwable) {
            is SocketException -> sendNetworkResultEvent(NetworkState.BAD_INTERNET)
            is HttpException -> sendNetworkResultEvent(NetworkState.PARSE_ERROR)
            is UnknownHostException -> sendNetworkResultEvent(NetworkState.WRONG_CONNECTION)
            else -> sendNetworkResultEvent(NetworkState.OTHER_ERROR)
        }
    }

    private fun sendNetworkResultEvent(state: NetworkState) {
        viewModelScope.launch {
            _networkState.emit(state)
        }
    }

    sealed class NavigationEvent {
        data class SelectEvent(val isSelected: Boolean) : NavigationEvent()
        data class RemoveEvent(val isRemoved: Boolean) : NavigationEvent()
    }
}

