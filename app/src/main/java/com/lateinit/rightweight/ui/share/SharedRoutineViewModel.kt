package com.lateinit.rightweight.ui.share

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.lateinit.rightweight.data.mapper.local.toSharedRoutineSortType
import com.lateinit.rightweight.data.repository.SharedRoutineRepository
import com.lateinit.rightweight.ui.login.NetworkState
import com.lateinit.rightweight.ui.mapper.toSharedRoutineUiModel
import com.lateinit.rightweight.ui.model.shared.SharedRoutineSortTypeUiModel
import com.lateinit.rightweight.ui.model.shared.SharedRoutineUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.net.SocketException
import java.net.UnknownHostException
import javax.inject.Inject

@HiltViewModel
class SharedRoutineViewModel @Inject constructor(
    private val sharedRoutineRepository: SharedRoutineRepository
) : ViewModel() {
    private val _uiState =
        MutableStateFlow<LatestSharedRoutineUiState>(LatestSharedRoutineUiState.Success(PagingData.empty()))
    val uiState: StateFlow<LatestSharedRoutineUiState> = _uiState

    val networkExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        when (throwable) {
            is SocketException -> sendNetworkResultEvent(NetworkState.BAD_INTERNET)
            is HttpException -> sendNetworkResultEvent(NetworkState.PARSE_ERROR)
            is UnknownHostException -> sendNetworkResultEvent(NetworkState.WRONG_CONNECTION)
            else -> sendNetworkResultEvent(NetworkState.OTHER_ERROR)
        }
    }

    init {
        getSharedRoutinesByPaging()
    }

    private fun sendNetworkResultEvent(state: NetworkState) {
        viewModelScope.launch {
            _uiState.value = LatestSharedRoutineUiState.Error(state)
        }
    }

    private fun getSharedRoutinesByPaging() {
        viewModelScope.launch(networkExceptionHandler) {
            sharedRoutineRepository.getSharedRoutinesByPaging().cachedIn(this)
                .collect { sharedRoutinePagingData ->
                    val sharedRoutines = sharedRoutinePagingData.map { sharedRoutine ->
                        sharedRoutine.toSharedRoutineUiModel()
                    }
                    _uiState.value = LatestSharedRoutineUiState.Success(sharedRoutines)
                }
        }
    }

    fun setSharedRoutineSortType(sortTypeUiModel: SharedRoutineSortTypeUiModel) {
        getSharedRoutinesByPaging()
        viewModelScope.launch {
            sharedRoutineRepository.setSharedRoutineSortType(sortTypeUiModel.toSharedRoutineSortType())
        }
    }

}

sealed class LatestSharedRoutineUiState {
    data class Success(val sharedRoutines: PagingData<SharedRoutineUiModel>) :
        LatestSharedRoutineUiState()

    data class Error(val state: NetworkState) : LatestSharedRoutineUiState()
}
