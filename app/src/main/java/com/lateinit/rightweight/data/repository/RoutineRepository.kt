package com.lateinit.rightweight.data.repository

import com.lateinit.rightweight.data.database.entity.Day
import com.lateinit.rightweight.data.database.entity.Exercise
import com.lateinit.rightweight.data.database.entity.ExerciseSet
import com.lateinit.rightweight.data.database.entity.Routine
import com.lateinit.rightweight.data.database.intermediate.DayWithExercises
import com.lateinit.rightweight.data.database.intermediate.RoutineWithDays

interface RoutineRepository {

    suspend fun insertRoutine(
        routine: Routine,
        days: List<Day>,
        exercises: List<Exercise>,
        sets: List<ExerciseSet>
    )

    suspend fun updateRoutines(routines: List<Routine>)
    suspend fun getRoutineById(routineId: String): Routine
    suspend fun getHigherRoutineOrder(): Int?
    suspend fun getDaysByRoutineId(routineId: String): List<Day>
    suspend fun getDayById(dayId: String): Day
    suspend fun getExercisesByDayId(dayId: String): List<Exercise>
    suspend fun getSetsByExerciseId(exerciseId: String): List<ExerciseSet>
    suspend fun getRoutines(): List<Routine>

    suspend fun getRoutineWithDaysByRoutineId(routineId: String): RoutineWithDays
    suspend fun getDayWithExercisesByDayId(dayId: String): DayWithExercises
    suspend fun removeRoutineById(routineId: String)
    suspend fun shareRoutine(userId: String, routine: Routine)
}