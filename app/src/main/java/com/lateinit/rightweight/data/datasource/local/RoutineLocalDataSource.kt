package com.lateinit.rightweight.data.datasource.local

import com.lateinit.rightweight.data.database.entity.Day
import com.lateinit.rightweight.data.database.entity.Exercise
import com.lateinit.rightweight.data.database.entity.ExerciseSet
import com.lateinit.rightweight.data.database.entity.Routine
import com.lateinit.rightweight.data.database.intermediate.DayWithExercises
import com.lateinit.rightweight.data.database.intermediate.RoutineWithDays
import kotlinx.coroutines.flow.Flow

interface RoutineLocalDataSource {

    suspend fun insertRoutine(
        routine: Routine,
        days: List<Day>,
        exercises: List<Exercise>,
        sets: List<ExerciseSet>
    )

    suspend fun restoreRoutines(
        routine: List<Routine>,
        days: List<Day>,
        exercises: List<Exercise>,
        sets: List<ExerciseSet>
    )

    suspend fun getRoutineById(routineId: String): Routine

    suspend fun getHigherRoutineOrder(): Int?

    suspend fun getDayById(dayId: String): Day

    suspend fun getDaysByRoutineId(routineId: String): List<Day>

    suspend fun getExercisesByDayId(dayId: String): List<Exercise>

    suspend fun getSetsByExerciseId(exerciseId: String): List<ExerciseSet>

    suspend fun getRoutineWithDaysByRoutineId(routineId: String): RoutineWithDays

    suspend fun getAllRoutineWithDays(): List<RoutineWithDays>

    fun getAllRoutines(): Flow<List<Routine>>

    fun getDayWithExercisesByDayId(dayId: String): Flow<DayWithExercises>

    suspend fun updateRoutines(routines: List<Routine>)

    suspend fun removeRoutineById(routineId: String)

    suspend fun removeAllRoutines()
}