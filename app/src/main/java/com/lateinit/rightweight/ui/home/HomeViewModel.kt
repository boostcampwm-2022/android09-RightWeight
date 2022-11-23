package com.lateinit.rightweight.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lateinit.rightweight.data.database.entity.Day
import com.lateinit.rightweight.data.database.entity.Exercise
import com.lateinit.rightweight.data.repository.RoutineRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val routineRepository: RoutineRepository
) : ViewModel() {

    private val _exercises = MutableLiveData<List<Exercise>>()
    val exercises: LiveData<List<Exercise>> get() = _exercises

    private val _day = MutableLiveData<Day>()
    val day: LiveData<Day> get() = _day


    fun getDay(dayId: String?) {
        dayId ?: return

        viewModelScope.launch {
            val day = routineRepository.getDayById(dayId)
            _day.postValue(day)
            val exercises = routineRepository.getExercisesByDayId(dayId)
            _exercises.postValue(exercises)
        }
    }

}