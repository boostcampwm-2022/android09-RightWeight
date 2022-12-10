package com.lateinit.rightweight.data.repository

import com.lateinit.rightweight.data.database.entity.Day
import com.lateinit.rightweight.data.database.entity.Exercise
import com.lateinit.rightweight.data.database.entity.ExerciseSet
import com.lateinit.rightweight.data.database.entity.Routine
import com.lateinit.rightweight.data.database.intermediate.DayWithExercises
import com.lateinit.rightweight.data.database.intermediate.RoutineWithDays
import com.lateinit.rightweight.ui.model.routine.RoutineUiModel
import kotlinx.coroutines.flow.Flow

interface RoutineRepository {

    suspend fun insertRoutine(
        routine: RoutineUiModel,
        days: List<Day>,
        exercises: List<Exercise>,
        sets: List<ExerciseSet>
    )

    suspend fun getRoutineById(routineId: String): Routine

    suspend fun getHigherRoutineOrder(): Int?

    suspend fun getDayById(dayId: String): Day

    suspend fun getExercisesByDayId(dayId: String): List<Exercise>

    suspend fun getSetsByExerciseId(exerciseId: String): List<ExerciseSet>

    suspend fun getRoutineWithDaysByRoutineId(routineId: String): RoutineWithDays

    suspend fun getUserRoutineIds(userId: String): List<String>

    suspend fun getAllRoutineWithDays(): List<RoutineWithDays>

    fun getAllRoutines(): Flow<List<Routine>>

    fun getDayWithExercisesByDayId(dayId: String): Flow<DayWithExercises>

    suspend fun updateRoutines(routines: List<RoutineUiModel>)

    suspend fun removeRoutineById(routineId: String)
}