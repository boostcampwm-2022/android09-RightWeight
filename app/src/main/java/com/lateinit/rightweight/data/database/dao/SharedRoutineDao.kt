package com.lateinit.rightweight.data.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.lateinit.rightweight.data.database.entity.SharedDay
import com.lateinit.rightweight.data.database.entity.SharedExercise
import com.lateinit.rightweight.data.database.entity.SharedExerciseSet
import com.lateinit.rightweight.data.database.entity.SharedRoutine

@Dao
interface SharedRoutineDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSharedRoutine(
        sharedRoutine: SharedRoutine,
//        days: List<SharedDay>,
//        exercises: List<SharedExercise>,
//        sets: List<SharedExerciseSet>
    )

    @Query("DELETE FROM shared_routine")
    fun removeAllSharedRoutines()

    @Query("SELECT * FROM shared_routine")
    fun getAllSharedRoutinesByPaging(): PagingSource<Int, SharedRoutine>

    @Query("SELECT * FROM shared_routine")
    fun getAllSharedRoutines(): List<SharedRoutine>
}