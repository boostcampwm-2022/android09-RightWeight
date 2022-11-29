package com.lateinit.rightweight.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lateinit.rightweight.data.database.entity.Exercise
import com.lateinit.rightweight.data.database.entity.ExerciseSet
import com.lateinit.rightweight.data.database.entity.History
import com.lateinit.rightweight.data.repository.HistoryRepository
import com.lateinit.rightweight.data.repository.RoutineRepository
import com.lateinit.rightweight.ui.model.DayUiModel
import com.lateinit.rightweight.util.toDayUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val routineRepository: RoutineRepository,
    private val historyRepository: HistoryRepository
) : ViewModel() {

    private val _exercises = MutableLiveData<List<Exercise>>()
    val exercises: LiveData<List<Exercise>> get() = _exercises

    private val _exerciseSets = MutableLiveData<List<ExerciseSet>>()
    val exerciseSets: LiveData<List<ExerciseSet>> get() = _exerciseSets

    private val _dayUiModel = MutableLiveData<DayUiModel?>()
    val dayUiModel: LiveData<DayUiModel?> get() = _dayUiModel

    fun getDayWithExercisesByDayId(dayId: String?) {
        if(dayId == null){
            _dayUiModel.postValue(null)
        }
        else {
            viewModelScope.launch {
                val dayWithExercises = routineRepository.getDayWithExercisesByDayId(dayId)
                val dayUiModel = dayWithExercises.day.toDayUiModel(
                    dayWithExercises.day.order,
                    dayWithExercises.exercises
                )
                _dayUiModel.postValue(dayUiModel)
            }
        }
    }

    suspend fun loadTodayHistory(): Flow<List<History>> {
        return historyRepository.loadHistoryByDate(LocalDate.now())
    }

    fun saveHistory() {
        val dayId = dayUiModel.value?.dayId ?: return
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