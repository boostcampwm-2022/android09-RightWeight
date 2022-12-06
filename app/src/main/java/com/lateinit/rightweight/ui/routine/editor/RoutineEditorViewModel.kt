package com.lateinit.rightweight.ui.routine.editor

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.lateinit.rightweight.data.database.entity.Routine
import com.lateinit.rightweight.data.repository.RoutineRepository
import com.lateinit.rightweight.ui.model.DayUiModel
import com.lateinit.rightweight.ui.model.ExercisePartTypeUiModel
import com.lateinit.rightweight.ui.model.ExerciseSetUiModel
import com.lateinit.rightweight.ui.model.ExerciseUiModel
import com.lateinit.rightweight.util.FIRST_DAY_POSITION
import com.lateinit.rightweight.util.toDay
import com.lateinit.rightweight.util.toDayUiModel
import com.lateinit.rightweight.util.toExercise
import com.lateinit.rightweight.util.toExerciseSet
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.util.UUID
import java.util.LinkedList
import javax.inject.Inject

@HiltViewModel
class RoutineEditorViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val routineRepository: RoutineRepository
) : ViewModel() {

    private var routineId: String

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

    init {
        this.routineId = savedStateHandle.get<String>("routineId") ?: DEFAULT_ROUTINE_ID

        if (routineId.isEmpty()) {
            this.routineId = createUUID()
            addDay()
        } else {
            getRoutine(routineId)
        }
    }

    fun addDay() {
        val days = _days.value ?: return
        val day = DayUiModel(createUUID(), routineId, days.size, true)

        days.add(day)
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
        val exercise = ExerciseUiModel(
            exerciseId = createUUID(),
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
        val exerciseSet = ExerciseSetUiModel(
            setId = createUUID(),
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
            val order: Int = if (routineOrder == null) {
                val higherOrder = routineRepository.getHigherRoutineOrder()
                if (higherOrder == null) 0 else higherOrder + 1
            } else {
                routineOrder
            }

            if (title == null || title.isEmpty()) return@launch
            if (description == null || description.isEmpty()) return@launch

            val days = _days.value ?: return@launch

            if (days.isEmpty()) return@launch

            val exercises = dayToExercise.value?.values?.flatMap { exercises ->
                if (exercises.isEmpty()) return@launch
                exercises
            } ?: return@launch
            val exerciseSets = exercises.flatMap { exercise ->
                if (exercise.title.isEmpty()) return@launch
                if (exercise.exerciseSets.isEmpty()) return@launch
                exercise.exerciseSets
            }

            routineRepository.insertRoutine(
                Routine(routineId, title, "author", description, LocalDateTime.now(), order),
                days.map { it.toDay() },
                exercises.map { it.toExercise() },
                exerciseSets.map { it.toExerciseSet() }
            )
        }
    }

    private fun createUUID(): String {
        return UUID.randomUUID().toString()
    }

    private fun getRoutine(routineId: String) {
        viewModelScope.launch {
            val routineWithDays = routineRepository.getRoutineWithDaysByRoutineId(routineId)
            val dayUiModels = routineWithDays.days.mapIndexed { index, routineWithDay ->
                routineWithDay.day.toDayUiModel(index, routineWithDay.exercises)
            }

            with(routineWithDays.routine) {
                routineTitle.value = title
                routineDescription.value = description
                routineOrder.value = order
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

    companion object {
        private const val DEFAULT_EXERCISE_TITLE = ""
        private const val DEFAULT_ROUTINE_ID = ""
    }
}