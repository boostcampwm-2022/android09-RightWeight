package com.lateinit.rightweight.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.lateinit.rightweight.data.database.entity.Day
import com.lateinit.rightweight.data.database.entity.Exercise
import com.lateinit.rightweight.data.database.entity.ExerciseSet
import com.lateinit.rightweight.data.database.entity.Routine
import com.lateinit.rightweight.data.database.intermediate.DayWithExercises
import com.lateinit.rightweight.data.database.intermediate.RoutineWithDays

@Dao
interface RoutineDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoutine(
        routine: Routine,
        days: List<Day>,
        exercises: List<Exercise>,
        sets: List<ExerciseSet>
    )

    @Update
    suspend fun updateRoutines(routines: List<Routine>)

    @Query("SELECT * FROM routine WHERE routine_id = :routineId")
    suspend fun getRoutineById(routineId: String): Routine

    @Query("SELECT (`order`) FROM routine ORDER BY `order` DESC LIMIT 1")
    suspend fun getHigherRoutineOrder(): Int?

    @Query("SELECT * FROM routine NATURAL JOIN day WHERE routine_id = :routineId ORDER BY `order`")
    suspend fun getDaysByRoutineId(routineId: String): List<Day>

    @Query("SELECT * FROM day WHERE day_id = :dayId")
    suspend fun getDayById(dayId: String): Day

    @Query("SELECT * FROM exercise WHERE day_id = :dayId ORDER BY `order`")
    suspend fun getExercisesByDayId(dayId: String): List<Exercise>

    @Query("SELECT * FROM exercise_set WHERE exercise_id = :exerciseId ORDER BY `order`")
    suspend fun getSetsByExerciseId(exerciseId: String): List<ExerciseSet>

    @Query("SELECT * FROM routine ORDER BY `order`")
    suspend fun getRoutines(): List<Routine>

    @Transaction
    @Query("SELECT * FROM routine WHERE routine_id = :routineId")
    suspend fun getRoutineWithDaysByRoutineId(routineId: String): RoutineWithDays

    @Transaction
    @Query("SELECT * FROM day WHERE day_id = :dayId")
    suspend fun getDayWithExercisesByDayId(dayId: String): DayWithExercises
}