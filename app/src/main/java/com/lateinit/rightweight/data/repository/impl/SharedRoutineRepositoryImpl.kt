package com.lateinit.rightweight.data.repository.impl

import androidx.paging.PagingData
import com.lateinit.rightweight.data.database.entity.SharedRoutine
import com.lateinit.rightweight.data.database.entity.SharedRoutineDay
import com.lateinit.rightweight.data.database.entity.SharedRoutineExercise
import com.lateinit.rightweight.data.database.entity.SharedRoutineExerciseSet
import com.lateinit.rightweight.data.database.intermediate.SharedRoutineWithDays
import com.lateinit.rightweight.data.database.mediator.SharedRoutineSortType
import com.lateinit.rightweight.data.datasource.SharedRoutineRemoteDataSource
import com.lateinit.rightweight.data.datasource.SharedRoutineLocalDataSource
import com.lateinit.rightweight.data.model.FieldTransformsModelData
import com.lateinit.rightweight.data.model.TransformData
import com.lateinit.rightweight.data.model.WriteModelData
import com.lateinit.rightweight.data.remote.model.IntValue
import com.lateinit.rightweight.data.repository.SharedRoutineRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SharedRoutineRepositoryImpl @Inject constructor(
    private val sharedRoutineRemoteDataSource: SharedRoutineRemoteDataSource,
    private val sharedRoutineLocalDataSource: SharedRoutineLocalDataSource
) : SharedRoutineRepository {

    override fun getSharedRoutinesByPaging(): Flow<PagingData<SharedRoutine>> {
        return sharedRoutineRemoteDataSource.getSharedRoutinesByPaging()
    }

    override suspend fun getChildrenDocumentName(path: String): List<String> {
        return sharedRoutineRemoteDataSource.getChildrenDocumentName(path)
    }

    override fun getSharedRoutineDetail(routineId: String): Flow<SharedRoutineWithDays> {
        return sharedRoutineLocalDataSource.getSharedRoutineWithDaysByRoutineId(routineId)
    }

    override suspend fun requestSharedRoutineDetail(routineId: String) {
        val sharedRoutineDays = mutableListOf<SharedRoutineDay>()
        val sharedRoutineExercises = mutableListOf<SharedRoutineExercise>()
        val sharedRoutineExerciseSets = mutableListOf<SharedRoutineExerciseSet>()

        sharedRoutineRemoteDataSource.getSharedRoutineDays(routineId).forEach { sharedRoutineDay ->
            sharedRoutineDays.add(sharedRoutineDay)
            sharedRoutineRemoteDataSource.getSharedRoutineExercises(routineId, sharedRoutineDay.dayId)
                .forEach { sharedRoutineExercise ->
                    sharedRoutineExercises.add(sharedRoutineExercise)
                    sharedRoutineRemoteDataSource.getSharedRoutineExerciseSets(
                        routineId,
                        sharedRoutineExercise.dayId,
                        sharedRoutineExercise.exerciseId
                    ).forEach { sharedRoutineExerciseSet ->
                        sharedRoutineExerciseSets.add(sharedRoutineExerciseSet)
                    }
                }
        }

        sharedRoutineLocalDataSource.insertSharedRoutineDetail(
            sharedRoutineDays.sortedBy { it.order },
            sharedRoutineExercises.sortedBy { it.order },
            sharedRoutineExerciseSets.sortedBy { it.order }
        )
    }

    override suspend fun commitTransaction(writes: List<WriteModelData>) {
        sharedRoutineRemoteDataSource.commitTransaction(writes)
    }

    override suspend fun isRoutineShared(routineId: String): Boolean {
        return sharedRoutineRemoteDataSource.getSharedRoutine(routineId) != null
    }

    override suspend fun increaseSharedCount(routineId: String) {
        val path =
            "${WriteModelData.defaultPath}/shared_routine/${routineId}"
        sharedRoutineRemoteDataSource.commitTransaction(
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
        sharedRoutineRemoteDataSource.setSharedRoutineSortType(sortType)
    }

}