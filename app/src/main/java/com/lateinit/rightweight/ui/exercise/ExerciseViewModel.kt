package com.lateinit.rightweight.ui.exercise

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lateinit.rightweight.data.repository.HistoryRepository
import com.lateinit.rightweight.ui.model.history.HistoryExerciseSetUiModel
import com.lateinit.rightweight.ui.model.history.HistoryExerciseUiModel
import com.lateinit.rightweight.util.toHistoryUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class ExerciseViewModel @Inject constructor(
    private val historyRepository: HistoryRepository
) : ViewModel() {

    val historyUiModel = historyRepository.getHistoryWithHistoryExercisesByDate(LocalDate.now()).map {
        it ?: return@map null
        it.toHistoryUiModel()
    }.stateIn(viewModelScope, SharingStarted.Lazily, null)

    val isAllHistorySetsChecked get() = verifyAllHistorySets()

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
            historyRepository.updateHistory(originHistoryUiModel.copy(time = time, completed = true))
        }
    }

    private fun verifyAllHistorySets(): Boolean {
        return historyUiModel.value?.exercises?.all { exercise ->
            exercise.exerciseSets.all { exerciseSet -> exerciseSet.checked }
        } ?: false
    }
}