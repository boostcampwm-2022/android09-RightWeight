package com.lateinit.rightweight.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lateinit.rightweight.data.database.entity.ExerciseSet
import com.lateinit.rightweight.data.repository.HistoryRepository
import com.lateinit.rightweight.data.repository.RoutineRepository
import com.lateinit.rightweight.data.repository.UserRepository
import com.lateinit.rightweight.util.toDayUiModel
import com.lateinit.rightweight.util.toRoutineUiModel
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
    private val routineRepository: RoutineRepository,
    private val historyRepository: HistoryRepository,
    userRepository: UserRepository
) : ViewModel() {

    private val userInfo =
        userRepository.getUser().stateIn(viewModelScope, SharingStarted.Eagerly, null)

    val selectedRoutine = userInfo.map {
        val routineId = it?.routineId
        if (routineId.isNullOrEmpty()) return@map null
        routineRepository.getRoutineById(routineId).toRoutineUiModel()
    }.stateIn(viewModelScope, SharingStarted.Lazily, null)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val dayWithExercises = userInfo.flatMapLatest {
        routineRepository.getDayWithExercisesByDayId(it?.dayId ?: "")
    }.stateIn(viewModelScope, SharingStarted.Lazily, null)

    val selectedDay = dayWithExercises.map {
        it ?: return@map null
        it.day.toDayUiModel(it.day.order, it.exercises)
    }.stateIn(viewModelScope, SharingStarted.Lazily, null)

    val todayHistory = historyRepository.getHistoryByDate(LocalDate.now())
        .stateIn(viewModelScope, SharingStarted.Lazily, null)

    fun saveHistory() {
        val routineId = userInfo.value?.routineId
        if (routineId.isNullOrEmpty()) return
        val dayId = selectedDay.value?.dayId
        if (dayId.isNullOrEmpty()) return
        viewModelScope.launch {
            val day = routineRepository.getDayById(dayId)
            val exercises = routineRepository.getExercisesByDayId(dayId)
            val totalExerciseSets = mutableListOf<ExerciseSet>()
            for (exercise in exercises) {
                totalExerciseSets.addAll(routineRepository.getSetsByExerciseId(exercise.exerciseId))
            }
            historyRepository.saveHistory(routineId, day, exercises, totalExerciseSets)
        }
    }

}