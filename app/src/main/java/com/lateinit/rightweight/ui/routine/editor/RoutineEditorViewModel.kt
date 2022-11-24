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
    private val routineRepository: RoutineRepository
) : ViewModel() {

    private val routineId = createUUID()

    val routineTitle = MutableLiveData<String>()
    val routineDescription = MutableLiveData<String>()

    private val currentDay = MutableLiveData<Day?>()

    private val _days = MutableLiveData<LinkedList<Day>>(LinkedList())
    val days: LiveData<List<Day>> = _days.map { days -> days.toList() }

    private val dayToExercise =
        MutableLiveData<MutableMap<String, LinkedList<Exercise>>>(mutableMapOf())
    private val exerciseToSet =
        MutableLiveData<MutableMap<String, LinkedList<ExerciseSet>>>(mutableMapOf())

    private val _dayExercises = MediatorLiveData<List<Exercise>>().apply {
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
    val dayExercises: LiveData<List<Exercise>> = _dayExercises

    init {
        addDay()
    }

    fun addDay() {
        val days = _days.value ?: return
        val day = Day(createUUID(), routineId, days.size)

        days.add(day)
        currentDay.value = day
        _days.value = days
    }

    fun removeDay() {
        val days = _days.value ?: return
        val dayPosition = currentDay.value?.order ?: return

        days.removeAt(dayPosition).also { day ->
            dayToExercise.value?.remove(day.dayId)
            day.exercises.forEach { exercise ->
                exerciseToSet.value?.remove(exercise.exerciseId)
            }
        }

        val reorderedDays = LinkedList(days.reordered())

        currentDay.value = when {
            reorderedDays.isEmpty() -> null
            dayPosition == reorderedDays.size -> reorderedDays.last
            else -> reorderedDays[dayPosition]
        }
        _days.value = reorderedDays
    }

    fun clickDay(dayPosition: Int) {
        val day = _days.value?.get(dayPosition) ?: return

        if (currentDay.value?.dayId == day.dayId) return

        currentDay.value = day
        _dayExercises.value = dayToExercise.value?.get(day.dayId)
    }

    fun moveUpDay(dayPosition: Int) {
        if (dayPosition == FIRST_DAY_POSITION) return

        val days = _days.value ?: return
        val prevPosition = dayPosition.dec()

        days[dayPosition] = days[prevPosition].copy(order = dayPosition).also {
            days[prevPosition] = days[dayPosition].copy(order = prevPosition)
        }
        _days.value = days
    }

    fun moveDownDay(dayPosition: Int) {
        if (dayPosition == _days.value?.lastIndex) return

        val days = _days.value ?: return
        val nextPosition = dayPosition.inc()

        days[dayPosition] = days[nextPosition].copy(order = dayPosition).also {
            days[nextPosition] = days[dayPosition].copy(order = nextPosition)
        }
        _days.value = days
    }

    fun addExercise() {
        val dayId = currentDay.value?.dayId ?: return
        val exercises = dayToExercise.value?.getOrDefault(dayId, LinkedList()) ?: return
        val exercise = Exercise(
            exerciseId = createUUID(),
            dayId = dayId,
            title = DEFAULT_EXERCISE_TITLE,
            order = exercises.size,
            part = ExercisePartType.CHEST
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
        exercisePartType: ExercisePartType
    ) {
        val exercises = dayToExercise.value?.get(dayId) ?: return

        exercises[exercisePosition] = exercises[exercisePosition].copy(part = exercisePartType)
        dayToExercise.value = dayToExercise.value
    }

    fun addExerciseSet(exerciseId: String) {
        val exerciseSets = exerciseToSet.value?.getOrDefault(exerciseId, LinkedList()) ?: return
        val exerciseSet = ExerciseSet(
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

            exerciseSets.forEach { exerciseSet ->
                if (exerciseSet.weight.isEmpty()) exerciseSet.weight = DEFAULT_SET_WEIGHT
                if (exerciseSet.count.isEmpty()) exerciseSet.count = DEFAULT_SET_COUNT
            }

            routineRepository.insertRoutine(
                Routine(routineId, title, "author", description, LocalDateTime.now()),
                days,
                exercises,
                exerciseSets
            )
        }
    }

    private fun createUUID(): String {
        return UUID.randomUUID().toString()
    }

    private fun updateDayExercises(): List<Exercise> {
        val dayId = currentDay.value?.dayId ?: return emptyList()
        val tempExercises = dayToExercise.value?.get(dayId) ?: return emptyList()
        val tempDayExercises = tempExercises.mapIndexed { exerciseIndex, exercise ->
            val exerciseSets =
                exerciseToSet.value?.get(exercise.exerciseId)?.reordered() ?: emptyList()
            val exerciseTitle =
                _dayExercises.value?.getOrNull(exerciseIndex)?.title ?: exercise.title

            exerciseToSet.value?.put(exercise.exerciseId, LinkedList(exerciseSets))
            exercise.copy(title = exerciseTitle, order = exerciseIndex, exerciseSets = exerciseSets)
        }

        dayToExercise.value?.put(dayId, LinkedList(tempDayExercises))
        return tempDayExercises
    }

    @JvmName("reorderedDays")
    private fun List<Day>.reordered(): List<Day> {
        return this.mapIndexed { index, day -> day.copy(order = index) }
    }

    @JvmName("reorderedExerciseSets")
    private fun List<ExerciseSet>.reordered(): List<ExerciseSet> {
        return this.mapIndexed { index, exerciseSet -> exerciseSet.copy(order = index) }
    }

    companion object {
        private const val FIRST_DAY_POSITION = 0
        private const val DEFAULT_EXERCISE_TITLE = ""
        private const val DEFAULT_SET_WEIGHT = "0"
        private const val DEFAULT_SET_COUNT = "0"
    }
}