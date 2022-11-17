package com.lateinit.rightweight.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.lateinit.rightweight.data.database.entity.Day
import com.lateinit.rightweight.data.database.entity.Exercise
import com.lateinit.rightweight.data.database.entity.ExerciseSet
import com.lateinit.rightweight.data.database.entity.Routine

@Dao
interface RoutineDao {

    suspend fun insertRoutine(
        routine: Routine,
        days: List<Day>,
        exercises: List<Exercise>,
        sets: List<ExerciseSet>
    ) {
        insertRoutineInfo(routine)
        insertDays(days)
        insertExercises(exercises)
        insertSets(sets)
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoutineInfo(routine: Routine)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDays(days: List<Day>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExercises(exercises: List<Exercise>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSets(sets: List<ExerciseSet>)
}