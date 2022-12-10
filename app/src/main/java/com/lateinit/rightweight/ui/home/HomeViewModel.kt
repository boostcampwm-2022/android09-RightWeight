package com.lateinit.rightweight.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lateinit.rightweight.data.database.entity.ExerciseSet
import com.lateinit.rightweight.data.repository.HistoryRepository
import com.lateinit.rightweight.data.repository.RoutineRepository
import com.lateinit.rightweight.data.repository.UserRepository
import com.lateinit.rightweight.ui.mapper.toDayUiModel
import com.lateinit.rightweight.ui.mapper.toRoutineUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val routineRepository: RoutineRepository,
    private val historyRepository: HistoryRepository,
) : ViewModel() {

    private val userInfo =
        userRepository.getUser().stateIn(viewModelScope, SharingStarted.Eagerly, null)

    val selectedRoutine = userInfo.map {
        val routineId = it?.routineId
        if (routineId.isNullOrEmpty()) return@map null
        routineRepository.getRoutineById(routineId).toRoutineUiModel()
    }.stateIn(viewModelScope, SharingStarted.Lazily, null)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val selectedDayWithExercises = userInfo.flatMapLatest {

        if (it == null) {
            routineRepository.getDayWithExercisesByDayId("")
        } else {
            todayHistory.flatMapLatest { history ->
                if (history?.completed == true) {
                    routineRepository.getDayWithExercisesByDayId(it.completedDayId)
                } else {
                    routineRepository.getDayWithExercisesByDayId(it.dayId)
                }
            }
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, null)

    val selectedDay = selectedDayWithExercises.map {
        it ?: return@map null
        it.day.toDayUiModel(it.day.order, it.exercises)
    }.stateIn(viewModelScope, SharingStarted.Lazily, null)

    val todayHistory = historyRepository.getHistoryByDate(LocalDate.now())
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    fun saveHistory() {
        val routine = selectedRoutine.value ?: return
        val dayId = selectedDay.value?.dayId

        val routineId = routine.routineId
        val routineTitle = routine.title

        if (routineId.isEmpty() || routineTitle.isEmpty() || dayId.isNullOrEmpty()) return

        viewModelScope.launch {
            val day = routineRepository.getDayById(dayId)
            val exercises = routineRepository.getExercisesByDayId(dayId)
            val totalExerciseSets = mutableListOf<ExerciseSet>()
            for (exercise in exercises) {
                totalExerciseSets.addAll(routineRepository.getSetsByExerciseId(exercise.exerciseId))
            }
            historyRepository.saveHistory(routineId, day, routineTitle, exercises, totalExerciseSets)
        }
    }

    fun resetRoutine() {
        val currentUser = userInfo.value ?: return
        val routineId = currentUser.routineId
        viewModelScope.launch {
            val days = routineRepository.getDaysByRoutineId(routineId)
            val firstDay = days.filter { it.order == 0 }[0]
            userRepository.saveUser(currentUser.copy(dayId = firstDay.dayId))
        }
    }

}