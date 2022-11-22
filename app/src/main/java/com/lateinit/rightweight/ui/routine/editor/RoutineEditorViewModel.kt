package com.lateinit.rightweight.ui.routine.editor

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.lateinit.rightweight.data.ExercisePartType
import com.lateinit.rightweight.data.database.entity.Day
import com.lateinit.rightweight.data.database.entity.Exercise
import com.lateinit.rightweight.data.database.entity.ExerciseSet
import com.lateinit.rightweight.data.database.entity.Routine
import com.lateinit.rightweight.data.repository.RoutineRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.util.UUID
import java.util.LinkedList
import javax.inject.Inject

@HiltViewModel
class RoutineEditorViewModel @Inject constructor(
    private val routineRepository: RoutineRepository,
) : ViewModel() {

    private val routineId = createUUID()

    val routineTitle = MutableLiveData<String>()
    val routineDescription = MutableLiveData<String>()

    private var currentDayId: String?

    private val _days = MutableLiveData<LinkedList<Day>>(LinkedList())
    val days: LiveData<List<Day>> = _days.map { days -> days.toList() }

    private val _exercises =
        MutableLiveData<MutableMap<String, LinkedList<Exercise>>>(mutableMapOf())
    private val _exerciseSets =
        MutableLiveData<MutableMap<String, LinkedList<ExerciseSet>>>(mutableMapOf())

    private val _dayExercises = MediatorLiveData<List<Exercise>>().apply {
        addSource(_exercises) {
            updateDayExercises()
        }
        addSource(_exerciseSets) {
            updateDayExercises()
        }
    }
    val dayExercises: LiveData<List<Exercise>> = _dayExercises

    init {
        addDay()
        currentDayId = _days.value?.first()?.dayId
        _dayExercises.value = _exercises.value?.get(currentDayId)
    }

    fun addDay() {
        val days = _days.value ?: return

        days.add(Day(createUUID(), routineId, days.size))
        _days.value = days
    }

    fun removeDay(dayPosition: Int) {
        val days = _days.value ?: return

        days.removeAt(dayPosition).also { day ->
            _exercises.value?.remove(day.dayId)
            day.exercises.forEach { exercise ->
                _exerciseSets.value?.remove(exercise.exerciseId)
            }
        }
        _days.value = days
    }

    fun clickDay(dayPosition: Int) {
        val day = _days.value?.get(dayPosition) ?: return

        if (currentDayId == day.dayId) return

        currentDayId = day.dayId
        _dayExercises.value = _exercises.value?.get(currentDayId)
    }

    fun moveUpDay(dayPosition: Int) {
        if (dayPosition == FIRST_DAY_POSITION) return

        val days = _days.value ?: return
        val prevPosition = dayPosition.dec()

        days[dayPosition] = days[prevPosition].also {
            days[prevPosition] = days[dayPosition]
        }
        _days.value = days
    }

    fun moveDownDay(dayPosition: Int) {
        if (dayPosition == _days.value?.lastIndex) return

        val days = _days.value ?: return
        val nextPosition = dayPosition.inc()

        days[dayPosition] = days[nextPosition].also {
            days[nextPosition] = days[dayPosition]
        }
        _days.value = days
    }

    fun addExercise() {
        val dayId = currentDayId ?: return
        val exercises = _exercises.value?.getOrDefault(dayId, LinkedList()) ?: return
        val exercise = Exercise(
            exerciseId = createUUID(),
            dayId = dayId,
            title = DEFAULT_EXERCISE_TITLE,
            order = exercises.size,
            part = ExercisePartType.CHEST
        )

        exercises.add(exercise)
        _exercises.value?.put(dayId, exercises)
        _exercises.value = _exercises.value
    }

    fun removeExercise(dayId: String, exercisePosition: Int) {
        val exercises = _exercises.value?.get(dayId) ?: return

        exercises.removeAt(exercisePosition).also { exercise ->
            _exerciseSets.value?.remove(exercise.exerciseId)
        }
        _exercises.value = _exercises.value
    }

    fun changeExercisePart(
        dayId: String,
        exercisePosition: Int,
        exercisePartType: ExercisePartType,
    ) {
        val exercises = _exercises.value?.get(dayId) ?: return

        exercises[exercisePosition] = exercises[exercisePosition].copy(part = exercisePartType)
        _exercises.value = _exercises.value
    }

    fun addExerciseSet(exerciseId: String) {
        val exerciseSets = _exerciseSets.value?.getOrDefault(exerciseId, LinkedList()) ?: return
        val exerciseSet = ExerciseSet(
            setId = createUUID(),
            exerciseId = exerciseId,
            order = exerciseSets.size
        )

        exerciseSets.add(exerciseSet)
        _exerciseSets.value?.put(exerciseId, exerciseSets)
        _exerciseSets.value = _exerciseSets.value
    }

    fun removeExerciseSet(exerciseId: String, exerciseSetPosition: Int) {
        val exerciseSets = _exerciseSets.value?.get(exerciseId) ?: return

        exerciseSets.removeAt(exerciseSetPosition)
        _exerciseSets.value = _exerciseSets.value
    }

    fun saveRoutine() {
        viewModelScope.launch {
            val title = routineTitle.value
            val description = routineDescription.value

            if (title == null || title.isEmpty()) return@launch
            if (description == null || description.isEmpty()) return@launch

            val days = _days.value ?: return@launch
            val exercises = _exercises.value?.values?.flatMap { exercises ->
                if (exercises.isEmpty()) return@launch
                exercises
            } ?: return@launch
            val exerciseSets = exercises.flatMap { exercise ->
                if (exercise.title.isEmpty()) return@launch
                if (exercise.exerciseSets.isEmpty()) return@launch
                exercise.exerciseSets
            }

            if (days.isEmpty()) return@launch

            routineRepository.insertRoutine(
                Routine(routineId, title, "author", description, LocalDateTime.now()),
                days.mapIndexed { index, day -> day.copy(order = index) },
                exercises.mapIndexed { index, exercise -> exercise.copy(order = index) },
                exerciseSets.mapIndexed { index, exerciseSet -> exerciseSet.copy(order = index) }
            )
        }
    }

    private fun createUUID(): String {
        return UUID.randomUUID().toString()
    }

    private fun updateDayExercises() {
        val tempExercises = _exercises.value?.get(currentDayId) ?: return

        _dayExercises.value =  tempExercises.mapIndexed { exerciseIndex, exercise ->
            val exerciseSets = _exerciseSets.value?.get(exercise.exerciseId) ?: emptyList()
            val exerciseTitle = _dayExercises.value?.getOrNull(exerciseIndex)?.title ?: exercise.title
            tempExercises[exerciseIndex].title = exerciseTitle
            exercise.copy(title = exerciseTitle, exerciseSets = exerciseSets.toList())
        }
    }

    companion object {
        private const val FIRST_DAY_POSITION = 0
        private const val DEFAULT_EXERCISE_TITLE = ""
    }
}