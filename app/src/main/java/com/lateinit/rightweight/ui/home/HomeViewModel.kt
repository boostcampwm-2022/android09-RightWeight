package com.lateinit.rightweight.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lateinit.rightweight.data.database.entity.ExerciseSet
import com.lateinit.rightweight.data.repository.HistoryRepository
import com.lateinit.rightweight.data.repository.RoutineRepository
import com.lateinit.rightweight.data.repository.UserRepository
import com.lateinit.rightweight.util.toDayUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val routineRepository: RoutineRepository,
    private val historyRepository: HistoryRepository,
    userRepository: UserRepository
) : ViewModel() {

    private val userInfo =
        userRepository.getUser().stateIn(viewModelScope, SharingStarted.Eagerly, null)

     val selectedRoutine = userInfo.map {
         it?.routineId ?: return@map null
         routineRepository.getRoutineById(it.routineId)
     }.stateIn(viewModelScope, SharingStarted.Lazily, null)

    val selectedDay = userInfo.map {
        it?.dayId ?: return@map null
        val dayWithExercises = routineRepository.getDayWithExercisesByDayId(it.dayId)
        dayWithExercises.day.toDayUiModel(
            dayWithExercises.day.order,
            dayWithExercises.exercises
        )
    }.stateIn(viewModelScope, SharingStarted.Lazily, null)

    val todayHistory = historyRepository.loadHistoryByDate(LocalDate.now())
        .stateIn(viewModelScope, SharingStarted.Lazily, null)

    fun saveHistory() {
        val dayId = selectedDay.value?.dayId ?: return
        viewModelScope.launch {
            val day = routineRepository.getDayById(dayId)
            val exercises = routineRepository.getExercisesByDayId(dayId)
            val totalExerciseSets = mutableListOf<ExerciseSet>()
            for (exercise in exercises) {
                totalExerciseSets.addAll(routineRepository.getSetsByExerciseId(exercise.exerciseId))
            }
            historyRepository.saveHistory(day, exercises, totalExerciseSets)
        }
    }

}