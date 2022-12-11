package com.lateinit.rightweight.ui.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lateinit.rightweight.data.repository.HistoryRepository
import com.lateinit.rightweight.ui.model.history.HistoryUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class CalendarViewModel @Inject constructor(
    private val historyRepository: HistoryRepository
) : ViewModel() {

    private val selectedDay = MutableStateFlow(LocalDate.now())
    private val currentMonth = MutableStateFlow(YearMonth.now())

    private val _routineTitle = MutableStateFlow(DEFAULT_ROUTINE_TITLE)
    val routineTitle: StateFlow<String> = _routineTitle.asStateFlow()

    private val _exerciseTime = MutableStateFlow(DEFAULT_EXERCISE_TIME)
    val exerciseTime: StateFlow<String> = _exerciseTime.asStateFlow()

    val dateToExerciseHistories = currentMonth.flatMapLatest {
        getHistoryBetweenDate(it)
    }.stateIn(viewModelScope, SharingStarted.Lazily, mapOf())

    val selectedDayInfo = selectedDay.combine(dateToExerciseHistories) { date, _ ->
        getSelectedDayInfo(date)
    }.stateIn(viewModelScope, SharingStarted.Lazily, null)

    fun selectDay(date: LocalDate) {
        selectedDay.value = date
    }

    fun changeMonth(date: LocalDate) {
        currentMonth.value = YearMonth.from(date)
    }

    private fun getSelectedDayInfo(date: LocalDate): HistoryUiModel? {
        return dateToExerciseHistories.value[date].also {
            _routineTitle.value = it?.routineTitle ?: DEFAULT_ROUTINE_TITLE
            _exerciseTime.value = it?.time ?: DEFAULT_EXERCISE_TIME
        }
    }

    private fun getHistoryBetweenDate(
        month: YearMonth
    ): Flow<Map<LocalDate, HistoryUiModel>> {
        val startDay = month.atDay(START_DAY_OF_MONTH)
        val endDay = month.atEndOfMonth()
        val startDayDiff =
            (DayOfWeek.values().size - (DayOfWeek.SUNDAY.value - startDay.dayOfWeek.value)).toLong()
        val endDayDiff = MONTH_TILE_COUNT - startDayDiff - endDay.dayOfMonth

        return historyRepository.getHistoryBetweenDate(
            startDay.minusDays(startDayDiff),
            endDay.plusDays(endDayDiff)
        )
    }

    companion object {
        private const val START_DAY_OF_MONTH = 1
        private const val MONTH_TILE_COUNT = 7 * 6
        private const val DEFAULT_ROUTINE_TITLE = ""
        private const val DEFAULT_EXERCISE_TIME = ""
    }
}