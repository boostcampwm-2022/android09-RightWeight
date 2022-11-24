package com.lateinit.rightweight.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lateinit.rightweight.data.database.entity.Day
import com.lateinit.rightweight.data.database.entity.Exercise
import com.lateinit.rightweight.data.database.entity.ExerciseSet
import com.lateinit.rightweight.data.repository.HistoryRepository
import com.lateinit.rightweight.data.repository.RoutineRepository
import dagger.hilt.android.lifecycle.HiltViewModel
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

    private val _day = MutableLiveData<Day>()
    val day: LiveData<Day> get() = _day

    private val _exerciseSets = MutableLiveData<List<ExerciseSet>>()
    val exerciseSets: LiveData<List<ExerciseSet>> get() = _exerciseSets


    fun getDay(dayId: String?) {
        dayId ?: return

        viewModelScope.launch {
            val day = routineRepository.getDayById(dayId)
            _day.postValue(day)
            val exercises = routineRepository.getExercisesByDayId(dayId)
            _exercises.postValue(exercises)
        }
    }

    fun checkTodayHistory(){
        viewModelScope.launch {
            val todayHistories = historyRepository.loadHistoryByDate(LocalDate.now())
            if(todayHistories.isEmpty()){
                saveHistory()
            }
        }
    }

    fun saveHistory(){
        val dayId = day.value?.dayId
        dayId ?: return
        viewModelScope.launch {
            val day = routineRepository.getDayById(dayId)
            val exercises = routineRepository.getExercisesByDayId(dayId)
            val totalExerciseSets = mutableListOf<ExerciseSet>()
            for(exercise in exercises){
                totalExerciseSets.addAll(routineRepository.getSetsByExerciseId(exercise.exerciseId))
            }
            historyRepository.saveHistory(day, exercises, totalExerciseSets)
        }
    }

}