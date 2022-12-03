package com.lateinit.rightweight.ui.share.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lateinit.rightweight.data.database.entity.SharedRoutineDay
import com.lateinit.rightweight.data.database.entity.SharedRoutineExercise
import com.lateinit.rightweight.data.database.entity.SharedRoutineExerciseSet
import com.lateinit.rightweight.data.repository.SharedRoutineRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SharedRoutineDetailViewModel @Inject constructor(
    private val sharedRoutineRepository: SharedRoutineRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(
        LatestSharedRoutineDetailUiState.Success(mutableMapOf(), mutableMapOf(), mutableMapOf())
    )
    val uiState: StateFlow<LatestSharedRoutineDetailUiState> = _uiState

    fun getSharedRoutineDetail(routineId: String) {
        viewModelScope.launch {
            val sharedRoutineDaysMap = mutableMapOf<String, List<SharedRoutineDay>>()
            val sharedRoutineExercisesMap = mutableMapOf<String, List<SharedRoutineExercise>>()
            val sharedRoutineExerciseSetsMap =
                mutableMapOf<String, List<SharedRoutineExerciseSet>>()

            sharedRoutineRepository.getSharedRoutineDays(routineId)
                .collect() { sharedRoutineDays ->
                    sharedRoutineDaysMap.put(routineId, sharedRoutineDays)
                    sharedRoutineDays.forEach() { sharedRoutineDay ->
                        sharedRoutineRepository.getSharedRoutineExercises(
                            routineId,
                            sharedRoutineDay.dayId
                        ).collect() { sharedRoutineExercises ->
                            sharedRoutineExercisesMap.put(
                                sharedRoutineDay.dayId,
                                sharedRoutineExercises
                            )
                            sharedRoutineExercises.forEach() { sharedRoutineExercise ->
                                sharedRoutineRepository.getSharedRoutineExerciseSets(
                                    routineId,
                                    sharedRoutineDay.dayId,
                                    sharedRoutineExercise.exerciseId
                                ).collect() { sharedRoutineExerciseSets ->
                                    sharedRoutineExerciseSetsMap.put(
                                        sharedRoutineExercise.exerciseId,
                                        sharedRoutineExerciseSets
                                    )
                                }
                            }
                        }
                    }
                    _uiState.value = LatestSharedRoutineDetailUiState.Success(
                        sharedRoutineDaysMap,
                        sharedRoutineExercisesMap,
                        sharedRoutineExerciseSetsMap
                    )
                }
        }
    }

}

sealed class LatestSharedRoutineDetailUiState {
    data class Success(
        val sharedRoutineDays: Map<String, List<SharedRoutineDay>>,
        val sharedRoutineExercises: Map<String, List<SharedRoutineExercise>>,
        val sharedRoutineExerciseSets: Map<String, List<SharedRoutineExerciseSet>>
    ) : LatestSharedRoutineDetailUiState()

    data class Error(val exception: Throwable) : LatestSharedRoutineDetailUiState()
}