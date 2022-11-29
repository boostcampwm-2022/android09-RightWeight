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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class ExerciseViewModel @Inject constructor(
    private val historyRepository: HistoryRepository
) : ViewModel() {

    private val _isAllHistorySetsChecked = MutableLiveData<Boolean>()
    val isAllHistorySetsChecked: LiveData<Boolean> get() = _isAllHistorySetsChecked

    suspend fun loadTodayHistory(): Flow<List<History>>{
        return historyRepository.loadHistoryByDate(LocalDate.now())
    }

    suspend fun loadHistoryExercises(historyId: String): Flow<List<HistoryExercise>> {
        return historyRepository.getHistoryExercisesByHistoryId(historyId)
    }

    suspend fun loadHistorySets(historyExerciseId: String): Flow<List<HistorySet>> {
        return historyRepository.getHistorySetsByHistoryExerciseId(historyExerciseId)
    }

    fun verifyAllHistorySets(historyExercises: List<HistoryExercise>){
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                _isAllHistorySetsChecked.postValue(historyRepository.verifyAllHistorySets(historyExercises))
            }
        }
    }

    fun updateHistory(history: History){
        viewModelScope.launch {
            historyRepository.updateHistory(history)
        }
    }

    fun updateHistorySet(historySet: HistorySet) {
        viewModelScope.launch {
            historyRepository.updateHistorySet(historySet)
        }
    }

    fun updateHistoryExercise(historyExercise: HistoryExercise) {
        viewModelScope.launch {
            historyRepository.updateHistoryExercise(historyExercise)
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

    fun addHistorySet(historyExerciseId: String) {
        viewModelScope.launch {
            historyRepository.addHistorySet(historyExerciseId)
        }
    }

    fun addHistoryExercise(historyId: String) {
        viewModelScope.launch {
            historyRepository.addHistoryExercise(historyId)
        }
    }
}