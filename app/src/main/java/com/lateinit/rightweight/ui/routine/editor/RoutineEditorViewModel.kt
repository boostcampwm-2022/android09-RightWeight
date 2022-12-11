package com.lateinit.rightweight.ui.routine.editor

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.lateinit.rightweight.data.mapper.local.toDay
import com.lateinit.rightweight.data.mapper.local.toExercise
import com.lateinit.rightweight.data.mapper.local.toExerciseSet
import com.lateinit.rightweight.data.mapper.local.toRoutine
import com.lateinit.rightweight.data.repository.RoutineRepository
import com.lateinit.rightweight.ui.mapper.toDayUiModel
import com.lateinit.rightweight.ui.mapper.toRoutineUiModel
import com.lateinit.rightweight.ui.model.routine.DayUiModel
import com.lateinit.rightweight.ui.model.routine.ExercisePartTypeUiModel
import com.lateinit.rightweight.ui.model.routine.ExerciseSetUiModel
import com.lateinit.rightweight.ui.model.routine.ExerciseUiModel
import com.lateinit.rightweight.ui.model.routine.RoutineUiModel
import com.lateinit.rightweight.util.DEFAULT_AUTHOR_NAME
import com.lateinit.rightweight.util.FIRST_DAY_POSITION
import com.lateinit.rightweight.util.createRandomUUID
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.util.LinkedList
import javax.inject.Inject

@HiltViewModel
class RoutineEditorViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val routineRepository: RoutineRepository
) : ViewModel() {

    private var routineId: String
    private var routineAuthor = DEFAULT_AUTHOR_NAME

    val routineTitle = MutableLiveData<String>()
    val routineDescription = MutableLiveData<String>()
    private val routineOrder = MutableLiveData<Int>()

    private val currentDayPosition = MutableLiveData<Int?>()
    private val currentDay = currentDayPosition.map {
        it ?: return@map null
        _days.value?.get(it)
    }

    private val _days = MutableLiveData<LinkedList<DayUiModel>>(LinkedList())
    val days: LiveData<List<DayUiModel>> = _days.map { it.toList() }

    private val dayToExercise =
        MutableLiveData<MutableMap<String, LinkedList<ExerciseUiModel>>>(mutableMapOf())
    private val exerciseToSet =
        MutableLiveData<MutableMap<String, LinkedList<ExerciseSetUiModel>>>(mutableMapOf())

    private val _dayExercises = MediatorLiveData<List<ExerciseUiModel>>().apply {
        addSource(currentDay) {
            value = updateDayExercises()
        }
        addSource(dayToExercise) {
            value = updateDayExercises()
        }
        addSource(exerciseToSet) {
            value = updateDayExercises()
        }
    }
    val dayExercises: LiveData<List<ExerciseUiModel>> = _dayExercises

    private val _isPossibleSaveRoutine = MutableSharedFlow<RoutineSaveState>()
    val isPossibleSaveRoutine = _isPossibleSaveRoutine.asSharedFlow()

    init {
        routineId = savedStateHandle.get<String>("routineId") ?: DEFAULT_ROUTINE_ID

        if (routineId.isEmpty()) {
            routineId = createRandomUUID()
            routineAuthor = savedStateHandle.get<String>("author") ?: DEFAULT_AUTHOR_NAME
            addDay()
        } else {
            getRoutine(routineId)
        }
    }

    fun addDay() {
        val days = _days.value ?: return

        if (days.size == MAX_DAY_SIZE) {
            sendEvent(RoutineSaveState.EXCEED_MAX_DAY_SIZE)
            return
        }

        val day = DayUiModel(createRandomUUID(), routineId, days.size, true)

        days.add(day)
        dayToExercise.value?.put(day.dayId, LinkedList())
        clickDay(days.lastIndex)
    }

    fun removeDay() {
        val days = _days.value ?: return
        val dayPosition = currentDayPosition.value ?: return

        days.removeAt(dayPosition).also { day ->
            dayToExercise.value?.remove(day.dayId)
            day.exercises.forEach { exercise ->
                exerciseToSet.value?.remove(exercise.exerciseId)
            }
        }

        val reorderedDays = LinkedList(days.reordered())

        currentDayPosition.value = when {
            reorderedDays.isEmpty() -> null
            dayPosition == reorderedDays.size -> reorderedDays.lastIndex
            else -> dayPosition
        }.also {
            it ?: return@also
            reorderedDays[it] = reorderedDays[it].copy(selected = true)
        }
        _days.value = reorderedDays
    }

    fun clickDay(dayPosition: Int) {
        val currentDayPosition = this.currentDayPosition.value

        if (currentDayPosition == dayPosition) return

        val originDays = _days.value ?: return
        val day = originDays[dayPosition].copy(selected = true)

        if (currentDayPosition != null) {
            originDays[currentDayPosition] = originDays[currentDayPosition].copy(selected = false)
        }

        originDays[dayPosition] = day
        this.currentDayPosition.value = dayPosition
        _days.value = originDays
    }

    fun addExercise() {
        val dayId = currentDay.value?.dayId ?: return
        val exercises = dayToExercise.value?.getOrDefault(dayId, LinkedList()) ?: return

        if (exercises.size == MAX_EXERCISE_SIZE) {
            sendEvent(RoutineSaveState.EXCEED_MAX_EXERCISE_SIZE)
            return
        }

        val exercise = ExerciseUiModel(
            exerciseId = createRandomUUID(),
            dayId = dayId,
            title = DEFAULT_EXERCISE_TITLE,
            order = exercises.size
        )

        exercises.add(exercise)
        dayToExercise.value?.put(dayId, exercises)
        dayToExercise.value = dayToExercise.value
    }

    fun removeExercise(dayId: String, exercisePosition: Int) {
        val exercises = dayToExercise.value?.get(dayId) ?: return

        exercises.removeAt(exercisePosition).also { exercise ->
            exerciseToSet.value?.remove(exercise.exerciseId)
        }
        dayToExercise.value = dayToExercise.value
    }

    fun changeExercisePart(
        dayId: String,
        exercisePosition: Int,
        exercisePartType: ExercisePartTypeUiModel
    ) {
        val exercises = dayToExercise.value?.get(dayId) ?: return

        exercises[exercisePosition] = exercises[exercisePosition].copy(part = exercisePartType)
        dayToExercise.value = dayToExercise.value
    }

    fun addExerciseSet(exerciseId: String) {
        val exerciseSets = exerciseToSet.value?.getOrDefault(exerciseId, LinkedList()) ?: return

        if (exerciseSets.size == MAX_EXERCISE_SET_SIZE) {
            sendEvent(RoutineSaveState.EXCEED_MAX_EXERCISE_SET_SIZE)
            return
        }

        val exerciseSet = ExerciseSetUiModel(
            setId = createRandomUUID(),
            exerciseId = exerciseId,
            order = exerciseSets.size
        )

        exerciseSets.add(exerciseSet)
        exerciseToSet.value?.put(exerciseId, exerciseSets)
        exerciseToSet.value = exerciseToSet.value
    }

    fun removeExerciseSet(exerciseId: String, exerciseSetPosition: Int) {
        val exerciseSets = exerciseToSet.value?.get(exerciseId) ?: return

        exerciseSets.removeAt(exerciseSetPosition)
        exerciseToSet.value = exerciseToSet.value
    }

    fun saveRoutine() {
        viewModelScope.launch {
            val title = routineTitle.value
            val description = routineDescription.value
            val routineOrder = routineOrder.value
            val days = days.value
            val order: Int = if (routineOrder == null) {
                val higherOrder = routineRepository.getHigherRoutineOrder()
                if (higherOrder == null) 0 else higherOrder + 1
            } else {
                routineOrder
            }

            if (title.isNullOrEmpty()) {
                sendEvent(RoutineSaveState.ROUTINE_TITLE_EMPTY)
                return@launch
            }
            if (description.isNullOrEmpty()) {
                sendEvent(RoutineSaveState.ROUTINE_DESCRIPTION_EMPTY)
                return@launch
            }
            if (days.isNullOrEmpty()) {
                sendEvent(RoutineSaveState.DAY_EMPTY)
                return@launch
            }

            val exercises = dayToExercise.value?.values?.flatMap { exercises ->
                if (exercises.isEmpty()) {
                    sendEvent(RoutineSaveState.EXERCISE_EMPTY)
                    return@launch
                }
                exercises
            } ?: run {
                sendEvent(RoutineSaveState.EXERCISE_EMPTY)
                return@launch
            }
            val exerciseSets = exercises.flatMap { exercise ->
                if (exercise.title.isEmpty()) {
                    sendEvent(RoutineSaveState.EXERCISE_TITLE_EMPTY)
                    return@launch
                }
                if (exercise.exerciseSets.isEmpty()) {
                    sendEvent(RoutineSaveState.EXERCISE_SET_EMPTY)
                    return@launch
                }
                exercise.exerciseSets
            }

            routineRepository.insertRoutine(
                RoutineUiModel(routineId, title, routineAuthor, description, LocalDateTime.now(), order).toRoutine(),
                days.map { it.toDay() },
                exercises.map { it.toExercise() },
                exerciseSets.map { it.toExerciseSet() }
            )
            sendEvent(RoutineSaveState.SUCCESS)
        }
    }

    private fun sendEvent(saveState: RoutineSaveState) {
        viewModelScope.launch {
            _isPossibleSaveRoutine.emit(saveState)
        }
    }

    private fun getRoutine(routineId: String) {
        viewModelScope.launch {
            val routineWithDays = routineRepository.getRoutineWithDaysByRoutineId(routineId)
            val dayUiModels = routineWithDays.days.mapIndexed { index, routineWithDay ->
                routineWithDay.day.toDayUiModel(index, routineWithDay.exercises)
            }

            with(routineWithDays.routine.toRoutineUiModel()) {
                routineTitle.value = title
                routineDescription.value = description
                routineOrder.value = order
                routineAuthor = author
            }
            mapDayToExercise(dayUiModels)
            mapExerciseToSet(dayUiModels.flatMap { it.exercises })
            _days.value = LinkedList(dayUiModels)
            currentDayPosition.value = FIRST_DAY_POSITION
        }
    }

    private fun updateDayExercises(): List<ExerciseUiModel> {
        val dayId = currentDay.value?.dayId ?: return emptyList()
        val tempExercises = dayToExercise.value?.get(dayId) ?: return emptyList()
        val tempDayExercises = tempExercises.mapIndexed { exerciseIndex, exercise ->
            val exerciseSets =
                exerciseToSet.value?.get(exercise.exerciseId) ?: emptyList()

            when {
                exerciseSets != exercise.exerciseSets -> {
                    val reorderedExerciseSets = exerciseSets.reordered()
                    exerciseToSet.value?.put(exercise.exerciseId, LinkedList(reorderedExerciseSets))
                    exercise.copy(exerciseSets = reorderedExerciseSets)
                }
                exerciseIndex != exercise.order -> {
                    exercise.copy(order = exerciseIndex)
                }
                else -> exercise
            }
        }

        dayToExercise.value?.put(dayId, LinkedList(tempDayExercises))
        return tempDayExercises
    }

    private fun mapDayToExercise(dayUiModels: List<DayUiModel>) {
        dayToExercise.value = dayUiModels.associate {
            it.dayId to LinkedList(it.exercises)
        }.toMutableMap()
    }

    private fun mapExerciseToSet(exerciseUiModels: List<ExerciseUiModel>) {
        exerciseToSet.value = exerciseUiModels.associate {
            it.exerciseId to LinkedList(it.exerciseSets)
        }.toMutableMap()
    }

    @JvmName("reorderedDays")
    private fun List<DayUiModel>.reordered(): List<DayUiModel> {
        return this.mapIndexed { index, day -> day.copy(order = index) }
    }

    @JvmName("reorderedExerciseSets")
    private fun List<ExerciseSetUiModel>.reordered(): List<ExerciseSetUiModel> {
        return this.mapIndexed { index, exerciseSet ->
            if (exerciseSet.order == index) exerciseSet else exerciseSet.copy(order = index)
        }
    }

    enum class RoutineSaveState {
        SUCCESS,
        ROUTINE_TITLE_EMPTY,
        ROUTINE_DESCRIPTION_EMPTY,
        EXERCISE_TITLE_EMPTY,
        DAY_EMPTY,
        EXERCISE_EMPTY,
        EXERCISE_SET_EMPTY,
        EXCEED_MAX_DAY_SIZE,
        EXCEED_MAX_EXERCISE_SIZE,
        EXCEED_MAX_EXERCISE_SET_SIZE
    }

    companion object {
        private const val DEFAULT_EXERCISE_TITLE = ""
        private const val DEFAULT_ROUTINE_ID = ""
        private const val MAX_DAY_SIZE = 10
        private const val MAX_EXERCISE_SIZE = 10
        private const val MAX_EXERCISE_SET_SIZE = 10
    }
}