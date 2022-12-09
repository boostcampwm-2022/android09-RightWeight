package com.lateinit.rightweight.data.datasource

import com.lateinit.rightweight.data.database.entity.Day
import com.lateinit.rightweight.data.database.entity.Exercise
import com.lateinit.rightweight.data.database.entity.ExerciseSet
import com.lateinit.rightweight.data.database.entity.Routine
import com.lateinit.rightweight.data.database.entity.SharedRoutineDay
import com.lateinit.rightweight.data.database.entity.SharedRoutineExercise
import com.lateinit.rightweight.data.database.entity.SharedRoutineExerciseSet
import com.lateinit.rightweight.data.database.intermediate.DayWithExercises
import com.lateinit.rightweight.data.database.intermediate.RoutineWithDays
import com.lateinit.rightweight.data.database.intermediate.SharedRoutineWithDays
import kotlinx.coroutines.flow.Flow

interface RoutineLocalDataSource {

    suspend fun insertRoutine(
        routine: Routine,
        days: List<Day>,
        exercises: List<Exercise>,
        sets: List<ExerciseSet>
    )

    suspend fun updateRoutines(routines: List<Routine>)
    suspend fun getHigherRoutineOrder(): Int?
    suspend fun getRoutineById(routineId: String): Routine
    suspend fun getDayById(dayId: String): Day
    suspend fun getExercisesByDayId(dayId: String): List<Exercise>
    suspend fun getSetsByExerciseId(exerciseId: String): List<ExerciseSet>
    fun getAllRoutines(): Flow<List<Routine>>
    suspend fun getRoutineWithDaysByRoutineId(routineId: String): RoutineWithDays
    fun getDayWithExercisesByDayId(dayId: String): Flow<DayWithExercises>
    suspend fun removeRoutineById(routineId: String)

    suspend fun insertSharedRoutineDetail(
        days: List<SharedRoutineDay>,
        exercises: List<SharedRoutineExercise>,
        sets: List<SharedRoutineExerciseSet>
    )
    fun getSharedRoutineWithDaysByRoutineId(routineId: String): Flow<SharedRoutineWithDays>
    fun getSelectedRoutine(routineId: String?): Flow<Routine>
}