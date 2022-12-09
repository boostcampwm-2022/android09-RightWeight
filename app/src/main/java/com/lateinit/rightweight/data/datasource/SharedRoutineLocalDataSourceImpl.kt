package com.lateinit.rightweight.data.datasource

import com.lateinit.rightweight.data.database.dao.SharedRoutineDao
import com.lateinit.rightweight.data.database.entity.SharedRoutineDay
import com.lateinit.rightweight.data.database.entity.SharedRoutineExercise
import com.lateinit.rightweight.data.database.entity.SharedRoutineExerciseSet
import com.lateinit.rightweight.data.database.intermediate.SharedRoutineWithDays
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SharedRoutineLocalDataSourceImpl @Inject constructor(
    private val sharedRoutineDao: SharedRoutineDao
): SharedRoutineLocalDataSource {
    override suspend fun insertSharedRoutineDetail(
        days: List<SharedRoutineDay>,
        exercises: List<SharedRoutineExercise>,
        sets: List<SharedRoutineExerciseSet>
    ) {
        sharedRoutineDao.insertSharedRoutineDetail(days, exercises, sets)
    }

    override fun getSharedRoutineWithDaysByRoutineId(routineId: String): Flow<SharedRoutineWithDays> {
        return sharedRoutineDao.getSharedRoutineWithDaysByRoutineId(routineId)
    }
}