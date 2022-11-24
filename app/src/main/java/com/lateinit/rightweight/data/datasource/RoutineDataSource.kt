package com.lateinit.rightweight.data.datasource

import com.lateinit.rightweight.data.database.entity.Day
import com.lateinit.rightweight.data.database.entity.Exercise
import com.lateinit.rightweight.data.database.entity.ExerciseSet
import com.lateinit.rightweight.data.database.entity.Routine

interface RoutineDataSource {

    suspend fun insertRoutine(
        routine: Routine,
        days: List<Day>,
        exercises: List<Exercise>,
        sets: List<ExerciseSet>
    )

    suspend fun getRoutineById(routineId: String): Routine
    suspend fun getDaysByRoutineId(routineId: String): List<Day>
    suspend fun getDayById(dayId: String): Day
    suspend fun getExercisesByDayId(dayId: String): List<Exercise>
    suspend fun getRoutines(): List<Routine>
}