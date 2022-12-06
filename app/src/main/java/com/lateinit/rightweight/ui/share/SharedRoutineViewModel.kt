package com.lateinit.rightweight.ui.share

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.lateinit.rightweight.data.repository.SharedRoutineRepository
import com.lateinit.rightweight.ui.model.SharedRoutineUiModel
import com.lateinit.rightweight.util.toSharedRoutineUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SharedRoutineViewModel @Inject constructor(
    private val sharedRoutineRepository: SharedRoutineRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(LatestSharedRoutineUiState.Success(PagingData.empty()))
    val uiState: StateFlow<LatestSharedRoutineUiState> = _uiState

    init {
        viewModelScope.launch {
            sharedRoutineRepository.getSharedRoutinesByPaging().map { sharedRoutinePagingData ->
               sharedRoutinePagingData.map { sharedRoutine ->
                    sharedRoutine.toSharedRoutineUiModel()
                }
            }.cachedIn(this).collect{
                _uiState.value = LatestSharedRoutineUiState.Success(it)
            }
        }
    }

}

sealed class LatestSharedRoutineUiState {
    data class Success(val sharedRoutines: PagingData<SharedRoutineUiModel>) :
        LatestSharedRoutineUiState()

    data class Error(val exception: Throwable) : LatestSharedRoutineUiState()
}