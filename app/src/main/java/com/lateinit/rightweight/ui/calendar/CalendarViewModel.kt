package com.lateinit.rightweight.ui.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lateinit.rightweight.data.database.intermediate.HistoryWithHistoryExercises
import com.lateinit.rightweight.data.database.intermediate.RoutineWithDays
import com.lateinit.rightweight.data.repository.HistoryRepository
import com.lateinit.rightweight.data.repository.RoutineRepository
import com.lateinit.rightweight.data.repository.UserRepository
import com.lateinit.rightweight.ui.model.DayUiModel
import com.lateinit.rightweight.ui.model.HistoryUiModel
import com.lateinit.rightweight.ui.model.ParentDayUiModel
import com.lateinit.rightweight.ui.model.RoutineUiModel
import com.lateinit.rightweight.util.toDayUiModel
import com.lateinit.rightweight.util.toHistoryUiModel
import com.lateinit.rightweight.util.toRoutineUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Period
import java.time.YearMonth
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class CalendarViewModel @Inject constructor(
    userRepository: UserRepository,
    private val historyRepository: HistoryRepository,
    private val routineRepository: RoutineRepository
) : ViewModel() {

    private val userInfo =
        userRepository.getUser().stateIn(viewModelScope, SharingStarted.Eagerly, null)

    private val selectedDay = MutableStateFlow(LocalDate.now())
    private val currentMonth = MutableStateFlow(YearMonth.now())

    private val _routineTitle = MutableStateFlow(DEFAULT_ROUTINE_TITLE)
    val routineTitle: StateFlow<String> = _routineTitle.asStateFlow()

    private val _exerciseTime = MutableStateFlow(DEFAULT_EXERCISE_TIME)
    val exerciseTime: StateFlow<String> = _exerciseTime.asStateFlow()

    private val currentRoutine = MutableStateFlow<RoutineUiModel?>(null)
    private val currentRoutineDays = MutableStateFlow<List<DayUiModel>>(emptyList())
    private var todayRoutineDayPosition = DAY_POSITION_NONE

    val dateToExerciseHistories = currentMonth.flatMapLatest {
        getHistoryBetweenDate(it)
    }.stateIn(viewModelScope, SharingStarted.Lazily, mapOf())

    val selectedDayInfo = combine(
        selectedDay,
        dateToExerciseHistories,
        currentRoutine,
        currentRoutineDays
    ) { date, _, _, _ ->
        getSelectedDayInfo(date)
    }.stateIn(viewModelScope, SharingStarted.Lazily, null)

    init {
        getRoutineInfo()
    }

    fun selectDay(date: LocalDate) {
        selectedDay.value = date
    }

    fun changeMonth(date: LocalDate) {
        currentMonth.value = YearMonth.from(date)
    }

    private fun getRoutineInfo() {
        viewModelScope.launch {
            userInfo.flatMapLatest { user ->
                val routineId = user?.routineId ?: return@flatMapLatest emptyFlow()

                if (routineId.isEmpty()) return@flatMapLatest emptyFlow()

                routineRepository.getRoutineWithDaysFlowByRoutineId(routineId)
            }.collect { routineWithDays ->
                separateRoutineInfo(routineWithDays)
            }
        }
    }

    private fun separateRoutineInfo(routineWithDays: RoutineWithDays) {
        currentRoutine.value = routineWithDays.routine.toRoutineUiModel()
        currentRoutineDays.value = routineWithDays.days.mapIndexed { index, routineWithDay ->
            if (routineWithDay.day.dayId == userInfo.value?.dayId) {
                todayRoutineDayPosition = index
            }
            routineWithDay.day.toDayUiModel(index, routineWithDay.exercises)
        }
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
            _routineTitle.value = it.routineTitle
            _exerciseTime.value = it.time
            it
        }
    }

    private fun getSelectedRoutineDay(date: LocalDate): DayUiModel? {
        val today = LocalDate.now()
        _exerciseTime.value = DEFAULT_EXERCISE_TIME

        return if (date.isBefore(today) || todayRoutineDayPosition == DAY_POSITION_NONE) {
            _routineTitle.value = DEFAULT_ROUTINE_TITLE
            null
        } else {
            val dayDiff = Period.between(today, date).days + todayRoutineDayPosition
            val currentRoutineDayPosition =
                dayDiff % currentRoutineDays.value.size

            _routineTitle.value = currentRoutine.value?.title ?: DEFAULT_ROUTINE_TITLE
            currentRoutineDays.value[currentRoutineDayPosition]
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
        ).mapDateToHistoryUiModels()
    }

    private fun Flow<List<HistoryWithHistoryExercises>>.mapDateToHistoryUiModels() =
        this.map { historyWithExercises ->
            historyWithExercises.associate { historyWithExercise ->
                historyWithExercise.history.date to historyWithExercise.toHistoryUiModel()
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