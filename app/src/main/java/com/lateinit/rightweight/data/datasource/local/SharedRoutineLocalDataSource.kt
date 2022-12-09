package com.lateinit.rightweight.data.datasource.local

import com.lateinit.rightweight.data.database.entity.SharedRoutineDay
import com.lateinit.rightweight.data.database.entity.SharedRoutineExercise
import com.lateinit.rightweight.data.database.entity.SharedRoutineExerciseSet
import com.lateinit.rightweight.data.database.intermediate.SharedRoutineWithDays
import kotlinx.coroutines.flow.Flow

interface SharedRoutineLocalDataSource {
    suspend fun insertSharedRoutineDetail(
        days: List<SharedRoutineDay>,
        exercises: List<SharedRoutineExercise>,
        sets: List<SharedRoutineExerciseSet>
    )

    fun getSharedRoutineWithDaysByRoutineId(routineId: String): Flow<SharedRoutineWithDays>
}