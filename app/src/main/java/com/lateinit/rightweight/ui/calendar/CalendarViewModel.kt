package com.lateinit.rightweight.ui.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lateinit.rightweight.data.database.entity.Routine
import com.lateinit.rightweight.data.database.intermediate.HistoryWithHistoryExercises
import com.lateinit.rightweight.data.model.User
import com.lateinit.rightweight.data.repository.HistoryRepository
import com.lateinit.rightweight.data.repository.RoutineRepository
import com.lateinit.rightweight.data.repository.UserRepository
import com.lateinit.rightweight.ui.model.DayUiModel
import com.lateinit.rightweight.ui.model.HistoryUiModel
import com.lateinit.rightweight.ui.model.ParentDayUiModel
import com.lateinit.rightweight.util.toDayUiModel
import com.lateinit.rightweight.util.toHistoryUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Period
import java.time.YearMonth
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val historyRepository: HistoryRepository,
    private val routineRepository: RoutineRepository
) : ViewModel() {

    private lateinit var user: User

    private val selectedDay = MutableStateFlow(LocalDate.MIN)
    private val currentMonth = MutableStateFlow(YearMonth.now())

    private val _routineTitle = MutableStateFlow(DEFAULT_ROUTINE_TITLE)
    val routineTitle: StateFlow<String> = _routineTitle.asStateFlow()

    private val _exerciseTime = MutableStateFlow(DEFAULT_EXERCISE_TIME)
    val exerciseTime: StateFlow<String> = _exerciseTime.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val selectedDayInfo = selectedDay.mapLatest {
        getSelectedDayInfo(it)
    }.stateIn(viewModelScope, SharingStarted.Lazily, null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val dateToExerciseHistories = currentMonth.flatMapLatest {
        getHistoryBetweenDate(it)
    }.stateIn(viewModelScope, SharingStarted.Lazily, mapOf())

    private var currentRoutine: Routine? = null
    private val currentRoutineDays = MutableStateFlow<List<DayUiModel>>(emptyList())
    private var todayRoutineDayPosition = DAY_POSITION_NONE

    init {
        viewModelScope.launch {
            user = userRepository.getUser()
            getRoutineDays()
        }
    }

    private fun getRoutineDays() {
        val routineId = user.routineId ?: return

        viewModelScope.launch {
            val routineWithDays = routineRepository.getRoutineWithDaysByRoutineId(routineId)

            currentRoutine = routineWithDays.routine
            currentRoutineDays.value = routineWithDays.days.mapIndexed { index, routineWithDay ->
                if (routineWithDay.day.dayId == user.dayId) todayRoutineDayPosition = index
                routineWithDay.day.toDayUiModel(index, routineWithDay.exercises)
            }
            selectDay(LocalDate.now())
        }
    }

    fun selectDay(date: LocalDate) {
        selectedDay.value = date
    }

    fun changeMonth(date: LocalDate) {
        currentMonth.value = YearMonth.from(date)
    }

    private fun getSelectedDayInfo(date: LocalDate): ParentDayUiModel? {
        return if (date in dateToExerciseHistories.value) {
            getSelectedHistory(date)
        } else {
            getSelectedRoutineDay(date)
        }
    }

    private fun getSelectedHistory(date: LocalDate): HistoryUiModel? {
        return dateToExerciseHistories.value[date]?.let {
            _routineTitle.value = it.history.routineTitle
            _exerciseTime.value = it.history.time
            it.toHistoryUiModel()
        }
    }

    private fun getSelectedRoutineDay(date: LocalDate): DayUiModel? {
        val today = LocalDate.now()
        _exerciseTime.value = DEFAULT_EXERCISE_TIME

        return if (date.isBefore(today) || todayRoutineDayPosition == DAY_POSITION_NONE) {
            _routineTitle.value = DEFAULT_ROUTINE_TITLE
            null
        } else {
            val dayDiff = Period.between(today, date).days
            val currentRoutineDayPosition =
                dayDiff % currentRoutineDays.value.size

            _routineTitle.value = currentRoutine?.title ?: DEFAULT_ROUTINE_TITLE
            currentRoutineDays.value[currentRoutineDayPosition]
        }
    }

    private fun getHistoryBetweenDate(
        month: YearMonth,
    ): Flow<Map<LocalDate, HistoryWithHistoryExercises>> {
        val startDay = month.atDay(START_DAY_OF_MONTH)
        val endDay = month.atEndOfMonth()
        val startDayDiff =
            (DayOfWeek.values().size - (DayOfWeek.SUNDAY.value - startDay.dayOfWeek.value)).toLong()
        val endDayDiff = MONTH_TILE_COUNT - startDayDiff - endDay.dayOfMonth

        return historyRepository.getHistoryBetweenDate(
            startDay.minusDays(startDayDiff),
            endDay.plusDays(endDayDiff)
        ).mapDateToHistoryWithExercises()
    }

    private fun Flow<List<HistoryWithHistoryExercises>>.mapDateToHistoryWithExercises() =
        this.map { historyWithExercises ->
            historyWithExercises.associateBy { historyWithExercise ->
                historyWithExercise.history.date
            }
        }

    companion object {
        private const val START_DAY_OF_MONTH = 1
        private const val MONTH_TILE_COUNT = 7 * 6
        private const val DAY_POSITION_NONE = -1
        private const val DEFAULT_ROUTINE_TITLE = ""
        private const val DEFAULT_EXERCISE_TIME = ""
    }
}