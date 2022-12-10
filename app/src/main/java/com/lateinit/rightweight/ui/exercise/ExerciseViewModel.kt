package com.lateinit.rightweight.ui.exercise

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lateinit.rightweight.data.database.entity.Day
import com.lateinit.rightweight.data.repository.HistoryRepository
import com.lateinit.rightweight.data.repository.RoutineRepository
import com.lateinit.rightweight.data.repository.UserRepository
import com.lateinit.rightweight.ui.mapper.toHistoryUiModel
import com.lateinit.rightweight.ui.model.history.HistoryExerciseSetUiModel
import com.lateinit.rightweight.ui.model.history.HistoryExerciseUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class ExerciseViewModel @Inject constructor(
    private val historyRepository: HistoryRepository,
    private val routineRepository: RoutineRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val userInfo =
        userRepository.getUser().stateIn(viewModelScope, SharingStarted.Eagerly, null)

    val historyUiModel =
        historyRepository.getHistoryWithHistoryExercisesByDate(LocalDate.now()).map {
            it ?: return@map null
            it.toHistoryUiModel()
        }.stateIn(viewModelScope, SharingStarted.Lazily, null)

    val isAllHistorySetsChecked get() = verifyAllHistorySets()

    private val _navigationEvent = MutableSharedFlow<Boolean>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    fun addHistorySet(historyExerciseId: String) {
        viewModelScope.launch {
            historyRepository.insertHistorySet(historyExerciseId)
        }
    }

    fun addHistoryExercise() {
        viewModelScope.launch {
            val historyId = historyUiModel.value?.historyId ?: return@launch
            historyRepository.insertHistoryExercise(historyId)
        }
    }

    fun updateHistorySet(historyExerciseSetUiModel: HistoryExerciseSetUiModel) {
        viewModelScope.launch {
            historyRepository.updateHistorySet(historyExerciseSetUiModel)
        }
    }

    fun updateHistoryExercise(historyExerciseUiModel: HistoryExerciseUiModel) {
        viewModelScope.launch {
            historyRepository.updateHistoryExercise(historyExerciseUiModel)
        }
    }

    fun removeHistorySet(historySetId: String) {
        viewModelScope.launch {
            historyRepository.removeHistorySet(historySetId)
        }
    }

    fun removeHistoryExercise(historyExerciseId: String) {
        viewModelScope.launch {
            historyRepository.removeHistoryExercise(historyExerciseId)
        }
    }

    fun endExercise(time: String) {
        viewModelScope.launch {
            val originHistoryUiModel = historyUiModel.value ?: return@launch
            historyRepository.updateHistory(
                originHistoryUiModel.copy(
                    time = time,
                    completed = true
                )
            )
            setCompletedDay()
            _navigationEvent.emit(true)
        }
    }

    private suspend fun setCompletedDay() {
        val currentUser = userInfo.value ?: return
        val nextDayId = getNextDayId(currentUser.routineId, currentUser.dayId)
        userRepository.saveUser(
            currentUser.copy(
                dayId = nextDayId,
                completedDayId = currentUser.dayId
            )
        )
    }

    private suspend fun getNextDayId(routineId: String, dayId: String): String {
        val days = routineRepository.getDaysByRoutineId(routineId)
        var currentDay: Day? = null
        days.forEach { day -> if (day.dayId == dayId) currentDay = day }
        currentDay?.let {
            if (it.order == days.size - 1) {
                return days[0].dayId
            } else {
                return days[it.order + 1].dayId
            }
        }
        return ""
    }

    private fun verifyAllHistorySets(): Boolean {
        return historyUiModel.value?.exercises?.all { exercise ->
            exercise.exerciseSets.all { exerciseSet -> exerciseSet.checked }
        } ?: false
    }
}