package com.lateinit.rightweight.data.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.lateinit.rightweight.data.database.intermediate.RoutineWithDays

@Dao
interface UserDao {

    @Transaction
    @Query("SELECT * FROM routine")
    suspend fun getAllRoutineWithDays(): List<RoutineWithDays>

}