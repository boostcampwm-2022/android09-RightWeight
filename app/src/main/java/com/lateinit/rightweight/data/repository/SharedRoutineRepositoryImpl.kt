package com.lateinit.rightweight.data.repository

import androidx.paging.PagingData
import com.lateinit.rightweight.data.database.entity.SharedRoutine
import com.lateinit.rightweight.data.datasource.RoutineRemoteDataSource
import com.lateinit.rightweight.data.remote.model.DayField
import com.lateinit.rightweight.data.remote.model.ExerciseField
import com.lateinit.rightweight.data.remote.model.ExerciseSetField
import com.lateinit.rightweight.data.remote.model.RootField
import com.lateinit.rightweight.util.toSharedRoutineField
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SharedRoutineRepositoryImpl @Inject constructor(
    private val routineRemoteDataSource: RoutineRemoteDataSource
): SharedRoutineRepository {
    override suspend fun getSharedRoutinesByPaging(): Flow<PagingData<SharedRoutine>> {
        return routineRemoteDataSource.getSharedRoutinesByPaging()
    }

    override suspend fun shareRoutine(userId: String, routineId: String, routine: Routine) {
        routineRemoteDataSource.shareRoutine(
            routineId, RootField(routine.toSharedRoutineField(userId))
        )
    }

    override suspend fun shareDay(routineId: String, dayId: String, dayField: DayField) {
        routineRemoteDataSource.shareDay(
            routineId, dayId, RootField(dayField)
        )
    }

    override suspend fun shareExercise(
        routineId: String,
        dayId: String,
        exerciseId: String,
        exerciseField: ExerciseField
    ) {
        routineRemoteDataSource.shareExercise(
            routineId, dayId, exerciseId, RootField(exerciseField)
        )
    }

    override suspend fun shareExerciseSet(
        routineId: String,
        dayId: String,
        exerciseId: String,
        exerciseSetId: String,
        exerciseSetField: ExerciseSetField
    ) {
        routineRemoteDataSource.shareExerciseSet(
            routineId, dayId, exerciseId, exerciseSetId, RootField(exerciseSetField)
        )
    }

    override suspend fun getChildrenDocumentName(path: String): List<String> {
        return routineRemoteDataSource.getChildrenDocumentName(path)
    }

    override suspend fun deleteDocument(path: String) {
        routineRemoteDataSource.deleteDocument(path)
    }
}