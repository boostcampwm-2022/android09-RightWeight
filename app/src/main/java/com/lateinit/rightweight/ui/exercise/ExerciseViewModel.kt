package com.lateinit.rightweight.ui.exercise

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lateinit.rightweight.data.database.entity.History
import com.lateinit.rightweight.data.database.entity.HistoryExercise
import com.lateinit.rightweight.data.database.entity.HistorySet
import com.lateinit.rightweight.data.repository.HistoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class ExerciseViewModel @Inject constructor(
    private val historyRepository: HistoryRepository
) : ViewModel() {

    private val _historyExercises = MutableLiveData<List<HistoryExercise>>()
    val historyExercises: LiveData<List<HistoryExercise>> get() = _historyExercises

    private val _history = MutableLiveData<History>()
    val history: LiveData<History> get() = _history

    private val _historySets = mutableMapOf<String, MutableLiveData<List<HistorySet>>>()
    val historySets: Map<String, LiveData<List<HistorySet>>> get() = _historySets

    fun loadTodayHistory() {
        viewModelScope.launch {
            val todayHistories = historyRepository.loadHistoryByDate(LocalDate.now())
            if (todayHistories.size == 1) {
                val todayHistory = todayHistories[0]
                _history.postValue(todayHistory)
                val historyExercises =
                    historyRepository.getHistoryExercisesByHistoryId(todayHistory.historyId)
                _historyExercises.postValue(historyExercises)
                for (historyExercise in historyExercises) {
                    val historySetsInHistoryExercise = MutableLiveData<List<HistorySet>>()
                    historySetsInHistoryExercise.postValue(
                        historyRepository.getHistorySetsByHistoryExerciseId(
                            historyExercise.exerciseId
                        )
                    )
                    _historySets.put(historyExercise.exerciseId, historySetsInHistoryExercise)
                }
            }
        }
    }

    fun saveHistorySet(historySet: HistorySet){
        viewModelScope.launch {
            historyRepository.saveHistorySet(historySet)
        }
    }

    fun removeHistorySet(historySetId: String){
        viewModelScope.launch {
            historyRepository.removeHistorySet(historySetId)
        }
    }

    fun addHistorySet(historyExerciseId: String){
        viewModelScope.launch {
            historyRepository.addHistorySet(historyExerciseId)
        }
    }
}