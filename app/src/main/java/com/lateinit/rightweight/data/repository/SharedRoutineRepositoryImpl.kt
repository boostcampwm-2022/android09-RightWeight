package com.lateinit.rightweight.data.repository

import androidx.paging.PagingData
import com.lateinit.rightweight.data.database.entity.SharedRoutine
import com.lateinit.rightweight.data.database.entity.SharedRoutineDay
import com.lateinit.rightweight.data.database.entity.SharedRoutineExercise
import com.lateinit.rightweight.data.database.entity.SharedRoutineExerciseSet
import com.lateinit.rightweight.data.database.intermediate.SharedRoutineWithDays
import com.lateinit.rightweight.data.database.mediator.SharedRoutineSortType
import com.lateinit.rightweight.data.datasource.RoutineLocalDataSource
import com.lateinit.rightweight.data.datasource.RoutineRemoteDataSource
import com.lateinit.rightweight.data.model.FieldTransformsModelData
import com.lateinit.rightweight.data.model.TransformData
import com.lateinit.rightweight.data.model.WriteModelData
import com.lateinit.rightweight.data.remote.model.IntValue
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SharedRoutineRepositoryImpl @Inject constructor(
    private val routineRemoteDataSource: RoutineRemoteDataSource,
    private val routineLocalDataSource: RoutineLocalDataSource
) : SharedRoutineRepository {
    override fun getSharedRoutinesByPaging(): Flow<PagingData<SharedRoutine>> {
        return routineRemoteDataSource.getSharedRoutinesByPaging()
    }

    override suspend fun getChildrenDocumentName(path: String): List<String> {
        return routineRemoteDataSource.getChildrenDocumentName(path)
    }

    override fun getSharedRoutineDetail(routineId: String): Flow<SharedRoutineWithDays> {
        return routineLocalDataSource.getSharedRoutineWithDaysByRoutineId(routineId)
    }

    override suspend fun requestSharedRoutineDetail(routineId: String) {
        val sharedRoutineDays = mutableListOf<SharedRoutineDay>()
        val sharedRoutineExercises = mutableListOf<SharedRoutineExercise>()
        val sharedRoutineExerciseSets = mutableListOf<SharedRoutineExerciseSet>()
        routineRemoteDataSource.getSharedRoutineDays(routineId).forEach() { sharedRoutineDay ->
            sharedRoutineDays.add(sharedRoutineDay)
            routineRemoteDataSource.getSharedRoutineExercises(routineId, sharedRoutineDay.dayId)
                .forEach() { sharedRoutineExercise ->
                    sharedRoutineExercises.add(sharedRoutineExercise)
                    routineRemoteDataSource.getSharedRoutineExerciseSets(
                        routineId,
                        sharedRoutineExercise.dayId,
                        sharedRoutineExercise.exerciseId
                    ).forEach() { sharedRoutineExerciseSet ->
                        sharedRoutineExerciseSets.add(sharedRoutineExerciseSet)
                    }
                }
        }
        routineLocalDataSource.insertSharedRoutineDetail(
            sharedRoutineDays,
            sharedRoutineExercises,
            sharedRoutineExerciseSets
        )
    }

    override suspend fun commitTransaction(writes: List<WriteModelData>) {
        routineRemoteDataSource.commitTransaction(writes)
    }

    override suspend fun checkRoutineInRemote(routineId: String): Boolean {
        return routineRemoteDataSource.getSharedRoutine(routineId) != null
    }

    override suspend fun increaseSharedCount(routineId: String) {
        val path =
            "${WriteModelData.defaultPath}/shared_routine/${routineId}"
        routineRemoteDataSource.commitTransaction(
            listOf(
                WriteModelData(
                    transform = TransformData(
                        path,
                        listOf(FieldTransformsModelData("shared_count.count", IntValue("1")))
                    )
                )
            )
        )
    }

    override suspend fun setSharedRoutineSortType(sortType: SharedRoutineSortType) {
        routineRemoteDataSource.setSharedRoutineSortType(sortType)
    }

}