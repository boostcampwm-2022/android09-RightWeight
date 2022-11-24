package com.lateinit.rightweight.ui.routine.editor

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.lateinit.rightweight.data.ExercisePartType
import com.lateinit.rightweight.data.database.entity.Routine
import com.lateinit.rightweight.data.repository.RoutineRepository
import com.lateinit.rightweight.ui.model.DayUiModel
import com.lateinit.rightweight.ui.model.ExerciseSetUiModel
import com.lateinit.rightweight.ui.model.ExerciseUiModel
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
    private val routineRepository: RoutineRepository
) : ViewModel() {

    private lateinit var routineId: String

    val routineTitle = MutableLiveData<String>()
    val routineDescription = MutableLiveData<String>()

    private val currentDay = MutableLiveData<DayUiModel?>()

    private val _days = MutableLiveData<LinkedList<DayUiModel>>(LinkedList())
    val days: LiveData<List<DayUiModel>> = _days.map { days -> days.toList() }

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

    fun init(routineId: String) {
        if (routineId.isEmpty()) {
            this.routineId = createUUID()
            addDay()
        } else {
            this.routineId = routineId
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
            dayPosition == reorderedDays.size -> {
                reorderedDays.last.copy(selected = true).also {
                    reorderedDays[reorderedDays.lastIndex] = it
                }
            }
            else -> {
                reorderedDays[dayPosition].copy(selected = true).also {
                    reorderedDays[dayPosition] = it
                }
            }
        }
        _days.value = reorderedDays
    }

    fun clickDay(dayPosition: Int) {
        val originDays = _days.value ?: return
        val day = originDays[dayPosition].copy(selected = true)

        currentDay.value?.order?.let {
            if (it == dayPosition) return
            originDays[it] = originDays[it].copy(selected = false)
        }

        originDays[dayPosition] = day
        currentDay.value = day
        _days.value = originDays
    }

    fun addExercise() {
        val dayId = currentDay.value?.dayId ?: return
        val exercises = dayToExercise.value?.getOrDefault(dayId, LinkedList()) ?: return
        val exercise = ExerciseUiModel(
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
                Routine(routineId, title, "author", description, LocalDateTime.now()),
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
            }
            mapDayToExercise(dayUiModels)
            mapExerciseToSet(dayUiModels.flatMap { it.exercises })
            _days.value = LinkedList(dayUiModels)
            currentDay.value = dayUiModels.first()
        }
    }

    private fun updateDayExercises(): List<ExerciseUiModel> {
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
        return this.mapIndexed { index, exerciseSet -> exerciseSet.copy(order = index) }
    }

    companion object {
        private const val DEFAULT_EXERCISE_TITLE = ""
    }
}