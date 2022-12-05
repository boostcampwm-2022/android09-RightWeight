package com.lateinit.rightweight.data.database.dao

import androidx.paging.PagingSource
import androidx.room.*
import com.lateinit.rightweight.data.database.entity.SharedRoutine
import com.lateinit.rightweight.data.database.entity.SharedRoutineDay
import com.lateinit.rightweight.data.database.entity.SharedRoutineExercise
import com.lateinit.rightweight.data.database.entity.SharedRoutineExerciseSet
import com.lateinit.rightweight.data.database.intermediate.SharedRoutineDayWithExercises
import com.lateinit.rightweight.data.database.intermediate.SharedRoutineWithDays
import kotlinx.coroutines.flow.Flow

@Dao
interface SharedRoutineDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSharedRoutine(
        sharedRoutine: SharedRoutine,
    )

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSharedRoutineDetail(
        days: List<SharedRoutineDay>,
        exercises: List<SharedRoutineExercise>,
        sets: List<SharedRoutineExerciseSet>
    )

    @Query("DELETE FROM shared_routine")
    fun removeAllSharedRoutines()

    @Query("SELECT * FROM shared_routine")
    fun getAllSharedRoutinesByPaging(): PagingSource<Int, SharedRoutine>

    @Query("SELECT * FROM shared_routine")
    fun getAllSharedRoutines(): List<SharedRoutine>

    @Transaction
    @Query("SELECT * FROM shared_routine WHERE routine_id = :routineId")
    fun getSharedRoutineWithDaysByRoutineId(routineId: String): Flow<SharedRoutineWithDays>
}