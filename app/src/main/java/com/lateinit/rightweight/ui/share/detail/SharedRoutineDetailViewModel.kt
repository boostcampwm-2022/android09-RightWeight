package com.lateinit.rightweight.ui.share.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lateinit.rightweight.data.database.entity.Day
import com.lateinit.rightweight.data.database.entity.Exercise
import com.lateinit.rightweight.data.database.entity.ExerciseSet
import com.lateinit.rightweight.data.mapper.local.toDayWithNewIds
import com.lateinit.rightweight.data.mapper.local.toExerciseSetWithNewIds
import com.lateinit.rightweight.data.mapper.local.toExerciseWithNewIds
import com.lateinit.rightweight.data.mapper.local.toRoutine
import com.lateinit.rightweight.data.repository.RoutineRepository
import com.lateinit.rightweight.data.repository.SharedRoutineRepository
import com.lateinit.rightweight.data.repository.UserRepository
import com.lateinit.rightweight.ui.login.NetworkState
import com.lateinit.rightweight.ui.mapper.toDayUiModel
import com.lateinit.rightweight.ui.mapper.toSharedRoutineUiModel
import com.lateinit.rightweight.ui.model.LoadingState
import com.lateinit.rightweight.ui.model.routine.DayUiModel
import com.lateinit.rightweight.ui.model.shared.SharedRoutineUiModel
import com.lateinit.rightweight.util.FIRST_DAY_POSITION
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.net.SocketException
import java.net.UnknownHostException
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class SharedRoutineDetailViewModel @Inject constructor(
    private val sharedRoutineRepository: SharedRoutineRepository,
    userRepository: UserRepository,
    private val routineRepository: RoutineRepository
) : ViewModel() {

    val userInfo = userRepository.getUser().stateIn(viewModelScope, SharingStarted.Eagerly, null)

    private val _uiState = MutableStateFlow<LatestSharedRoutineDetailUiState>(
        LatestSharedRoutineDetailUiState.Success(null, emptyList())
    )
    val uiState: StateFlow<LatestSharedRoutineDetailUiState> = _uiState

    private val _currentDayPosition = MutableLiveData<Int>()
    val currentDayPosition: LiveData<Int> = _currentDayPosition

    private val _navigationEvent = MutableSharedFlow<String>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    private val _loadingState = MutableSharedFlow<LoadingState>()
    val loadingState = _loadingState.asSharedFlow()

    private val networkExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        viewModelScope.launch{
            when (throwable) {
                is SocketException -> sendNetworkResultEvent(NetworkState.BAD_INTERNET)
                is HttpException -> sendNetworkResultEvent(NetworkState.PARSE_ERROR)
                is UnknownHostException -> sendNetworkResultEvent(NetworkState.WRONG_CONNECTION)
                else -> sendNetworkResultEvent(NetworkState.OTHER_ERROR)
            }
        }
    }

    private suspend fun sendNetworkResultEvent(state: NetworkState) {
        _uiState.value = LatestSharedRoutineDetailUiState.Error(state)
        if(state != NetworkState.SUCCESS){
            _loadingState.emit(LoadingState.FAIL)
        }
    }

    fun getSharedRoutineDetail(routineId: String) {
        viewModelScope.launch(networkExceptionHandler) {
            sharedRoutineRepository.getSharedRoutineDetail(routineId)
                .collect { sharedRoutineWithDays ->
                    if (sharedRoutineWithDays.days.isEmpty()) {
                        _loadingState.emit(LoadingState.GET)
                        sharedRoutineRepository.requestSharedRoutineDetail(routineId)
                        _loadingState.emit(LoadingState.NONE)
                    }

                    _uiState.value = LatestSharedRoutineDetailUiState.Success(
                        sharedRoutineWithDays.routine.toSharedRoutineUiModel(),
                        sharedRoutineWithDays.days.map { sharedRoutineWithDay ->
                            sharedRoutineWithDay.day.toDayUiModel(
                                sharedRoutineWithDay.exercises
                            )
                        }
                    ).apply {
                        if (this.dayUiModels.isNotEmpty()) {
                            initClickedDay(this)
                        }
                    }
                }
        }
    }

    private fun initClickedDay(successUiState: LatestSharedRoutineDetailUiState.Success) {
        val originDayUiModels = successUiState.dayUiModels.toMutableList()
        originDayUiModels[FIRST_DAY_POSITION] =
            originDayUiModels[FIRST_DAY_POSITION].copy(selected = true)
        _currentDayPosition.value = FIRST_DAY_POSITION
        _uiState.value = LatestSharedRoutineDetailUiState.Success(
            successUiState.sharedRoutineUiModel,
            originDayUiModels
        )
    }

    fun clickDay(dayPosition: Int) {
        if (_currentDayPosition.value == dayPosition
            || _uiState.value is LatestSharedRoutineDetailUiState.Error
        ) return

        val successUiState = _uiState.value as LatestSharedRoutineDetailUiState.Success
        val originDayUiModels = successUiState.dayUiModels.toMutableList()
        val lastSelectedPosition = _currentDayPosition.value ?: return

        originDayUiModels[lastSelectedPosition] =
            originDayUiModels[lastSelectedPosition].copy(selected = false)
        originDayUiModels[dayPosition] = originDayUiModels[dayPosition].copy(selected = true)

        _currentDayPosition.value = dayPosition
        _uiState.value = LatestSharedRoutineDetailUiState.Success(
            successUiState.sharedRoutineUiModel,
            originDayUiModels
        )
    }

    fun clickExercise(exercisePosition: Int) {
        if (_uiState.value is LatestSharedRoutineDetailUiState.Error) return

        val successUiState = _uiState.value as LatestSharedRoutineDetailUiState.Success
        val nowDayPosition = _currentDayPosition.value ?: return
        val originDayUiModels = successUiState.dayUiModels.toMutableList()
        val originExerciseUiModels = originDayUiModels[nowDayPosition].exercises.toMutableList()
        val exerciseUiModel = originExerciseUiModels[exercisePosition]

        originExerciseUiModels[exercisePosition] = exerciseUiModel.copy(
            expanded = exerciseUiModel.expanded.not()
        )

        originDayUiModels[nowDayPosition] =
            originDayUiModels[nowDayPosition].copy(exercises = originExerciseUiModels)

        _currentDayPosition.value = _currentDayPosition.value
        _uiState.value = LatestSharedRoutineDetailUiState.Success(
            successUiState.sharedRoutineUiModel,
            originDayUiModels
        )
    }

    fun importSharedRoutineToMyRoutines(): Boolean {
        if (_uiState.value is LatestSharedRoutineDetailUiState.Error) {
            return false
        }

        val successUiState = _uiState.value as LatestSharedRoutineDetailUiState.Success

        if (successUiState.sharedRoutineUiModel == null) {
            return false
        } else {
            viewModelScope.launch(networkExceptionHandler) {
                val sharedRoutineId = successUiState.sharedRoutineUiModel.routineId
                val routine = successUiState.sharedRoutineUiModel.toRoutine(
                    createUUID(),
                    userInfo.value?.displayName ?: "",
                    routineRepository.getHigherRoutineOrder()?.plus(1) ?: 0
                )
                val days = mutableListOf<Day>()
                val exercises = mutableListOf<Exercise>()
                val exerciseSets = mutableListOf<ExerciseSet>()

                successUiState.dayUiModels.forEach { dayUiModel ->
                    val dayId = createUUID()
                    days.add(dayUiModel.toDayWithNewIds(routine.routineId, dayId))
                    dayUiModel.exercises.forEach { exerciseUiModel ->
                        val exerciseId = createUUID()
                        exercises.add(exerciseUiModel.toExerciseWithNewIds(dayId, exerciseId))
                        exerciseUiModel.exerciseSets.forEach { exerciseSetUiModel ->
                            val exerciseSetId = createUUID()
                            exerciseSets.add(
                                exerciseSetUiModel.toExerciseSetWithNewIds(
                                    exerciseId,
                                    exerciseSetId
                                )
                            )
                        }
                    }
                }
                routineRepository.insertRoutine(routine, days, exercises, exerciseSets)
                _navigationEvent.emit(routine.routineId)
                sharedRoutineRepository.increaseSharedCount(sharedRoutineId)
            }
            return true
        }
    }

    private fun createUUID(): String {
        return UUID.randomUUID().toString()
    }

}

sealed class LatestSharedRoutineDetailUiState {
    data class Success(
        val sharedRoutineUiModel: SharedRoutineUiModel?,
        val dayUiModels: List<DayUiModel>
    ) : LatestSharedRoutineDetailUiState()

    data class Error(val state: NetworkState) : LatestSharedRoutineDetailUiState()
}

