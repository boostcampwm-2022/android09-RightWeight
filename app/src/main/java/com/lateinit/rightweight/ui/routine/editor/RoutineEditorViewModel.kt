package com.lateinit.rightweight.ui.routine.editor

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
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
import javax.inject.Inject

@HiltViewModel
class RoutineEditorViewModel @Inject constructor(
    private val routineRepository: RoutineRepository,
) : ViewModel() {

    private val routineId = createUUID()

    val routineTitle = MutableLiveData<String>()
    val routineDescription = MutableLiveData<String>()

    private val _days = MutableLiveData<List<Day>>()
    val days: LiveData<List<Day>> = _days

    private val dayMap = mutableMapOf<String, Day>()
    private val exerciseMap = mutableMapOf<String, Exercise>()

    init {
        addDay()
    }

    fun addDay() {
        val tempDays = _days.value?.toMutableList() ?: mutableListOf()
        val day = Day(createUUID(), routineId, tempDays.size)

        tempDays.add(day)
        dayMap[day.dayId] = day
        _days.value = tempDays
    }

    fun removeDay(dayPosition: Int) {
        val tempDays = _days.value?.toMutableList() ?: return

        tempDays.removeAt(dayPosition).also { day -> dayMap.remove(day.dayId) }
        _days.value = tempDays
    }

    fun moveUpDay(dayPosition: Int) {
        if (dayPosition == FIRST_DAY_POSITION) return

        val tempDays = _days.value?.toMutableList() ?: return
        val prevPosition = dayPosition.dec()

        tempDays[dayPosition] = tempDays[prevPosition].also {
            tempDays[prevPosition] = tempDays[dayPosition]
        }
        _days.value = tempDays
    }

    fun moveDownDay(dayPosition: Int) {
        if (dayPosition == _days.value?.lastIndex) return

        val tempDays = _days.value?.toMutableList() ?: return
        val nextPosition = dayPosition.inc()

        tempDays[dayPosition] = tempDays[nextPosition].also {
            tempDays[nextPosition] = tempDays[dayPosition]
        }
        _days.value = tempDays
    }

    fun addExercise(dayPosition: Int) {
        val tempDays = _days.value?.toMutableList() ?: return
        val tempDay = tempDays[dayPosition]
        val tempExercises = tempDay.exercises.toMutableList()
        val exercise = Exercise(
            exerciseId = createUUID(),
            dayId = tempDay.dayId,
            title = DEFAULT_EXERCISE_TITLE,
            order = tempExercises.size,
            part = ExercisePartType.CHEST
        )

        tempExercises.add(exercise)
        tempDays[dayPosition] = tempDay.copy(exercises = tempExercises).also { day ->
            dayMap[day.dayId] = day
        }
        exerciseMap[exercise.exerciseId] = exercise
        _days.value = tempDays
    }

    fun removeExercise(dayId: String, exercisePosition: Int) {
        val tempDays = _days.value?.toMutableList() ?: return
        val tempDay = dayMap[dayId] ?: return
        val tempExercises = tempDay.exercises.toMutableList()

        tempExercises.removeAt(exercisePosition).also { exercise ->
            exerciseMap.remove(exercise.exerciseId)
        }
        tempDays[tempDays.indexOf(tempDay)] = tempDay.copy(exercises = tempExercises).also { day ->
            dayMap[day.dayId] = day
        }
        _days.value = tempDays
    }

    fun changeExercisePart(
        dayId: String,
        exercisePosition: Int,
        exercisePartType: ExercisePartType
    ) {
        val tempDays = _days.value?.toMutableList() ?: return
        val tempDay = dayMap[dayId] ?: return
        val tempExercises = tempDay.exercises.toMutableList()
        val tempExercise = tempExercises[exercisePosition]


        tempExercises[exercisePosition] = tempExercise.copy(part = exercisePartType)
        tempDays[tempDays.indexOf(tempDay)] = tempDay.copy(exercises = tempExercises).also { day ->
            dayMap[day.dayId] = day
        }
        exerciseMap[tempExercise.exerciseId] = tempExercise
        _days.value = tempDays
    }

    fun addExerciseSet(exerciseId: String) {
        val tempDays = _days.value?.toMutableList() ?: return
        val dayId = exerciseMap[exerciseId]?.dayId ?: return
        val tempDay = dayMap[dayId] ?: return
        val tempExercises = tempDay.exercises.toMutableList()
        val tempExercise = exerciseMap[exerciseId] ?: return
        val tempExerciseSets = tempExercise.exerciseSets.toMutableList()
        val exerciseSet = ExerciseSet(
            setId = createUUID(),
            exerciseId = tempExercise.exerciseId,
            order = tempExerciseSets.size
        )

        tempExerciseSets.add(exerciseSet)
        tempExercises[tempExercises.indexOf(tempExercise)] =
            tempExercise.copy(exerciseSets = tempExerciseSets).also { exercise ->
                exerciseMap[exercise.exerciseId] = exercise
            }
        tempDays[tempDays.indexOf(tempDay)] = tempDay.copy(exercises = tempExercises).also { day ->
            dayMap[day.dayId] = day
        }
        _days.value = tempDays
    }

    fun removeExerciseSet(exerciseId: String, exerciseSetPosition: Int) {
        val tempDays = _days.value?.toMutableList() ?: return
        val dayId = exerciseMap[exerciseId]?.dayId ?: return
        val tempDay = dayMap[dayId] ?: return
        val tempExercises = tempDay.exercises.toMutableList()
        val tempExercise = exerciseMap[exerciseId] ?: return
        val tempExerciseSets = tempExercise.exerciseSets.toMutableList()


        tempExerciseSets.removeAt(exerciseSetPosition)
        tempExercises[tempExercises.indexOf(tempExercise)] =
            tempExercise.copy(exerciseSets = tempExerciseSets).also { exercise ->
                exerciseMap[exercise.exerciseId] = exercise
            }
        tempDays[tempDays.indexOf(tempDay)] = tempDay.copy(exercises = tempExercises).also { day ->
            dayMap[day.dayId] = day
        }
        _days.value = tempDays
    }

    fun saveRoutine() {
        viewModelScope.launch {
            _days.value?.let { days ->
                val title = routineTitle.value
                val description = routineDescription.value

                if (title == null || title.isEmpty()) return@launch
                if (description == null || description.isEmpty()) return@launch

                val exercises = days.map { day -> day.exercises }.flatten()
                val exerciseSets = exercises.map { exercise ->
                    if (exercise.title.isEmpty()) return@launch
                    exercise.exerciseSets
                }.flatten()

                routineRepository.insertRoutine(
                    Routine(routineId, title, "author", description, LocalDateTime.now()),
                    days,
                    exercises,
                    exerciseSets
                )
            }
        }
    }

    private fun createUUID(): String {
        return UUID.randomUUID().toString()
    }

    companion object {
        private const val FIRST_DAY_POSITION = 0
        private const val DEFAULT_EXERCISE_TITLE = ""
    }
}