package com.lateinit.rightweight.data.datasource

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.lateinit.rightweight.data.RoutineApiService
import com.lateinit.rightweight.data.database.AppDatabase
import com.lateinit.rightweight.data.database.AppPreferencesDataStore
import com.lateinit.rightweight.data.database.entity.SharedRoutineDay
import com.lateinit.rightweight.data.database.entity.SharedRoutineExercise
import com.lateinit.rightweight.data.database.entity.SharedRoutineExerciseSet
import com.lateinit.rightweight.data.database.mediator.SharedRoutineRemoteMediator
import com.lateinit.rightweight.data.model.WriteModelData
import com.lateinit.rightweight.data.model.WriteRequestBody
import com.lateinit.rightweight.util.toSharedRoutineDay
import com.lateinit.rightweight.util.toSharedRoutineExercise
import com.lateinit.rightweight.util.toSharedRoutineExerciseSet
import javax.inject.Inject

class RoutineRemoteDataSourceImpl @Inject constructor(
    private val db: AppDatabase,
    private val api: RoutineApiService,
    private val appPreferencesDataStore: AppPreferencesDataStore
) : RoutineRemoteDataSource {

    @OptIn(ExperimentalPagingApi::class)
    override fun getSharedRoutinesByPaging() = Pager(
        config = PagingConfig(10),
        remoteMediator = SharedRoutineRemoteMediator(
            db, api, appPreferencesDataStore
        ),
    ) {
        db.sharedRoutineDao().getAllSharedRoutinesByPaging()
    }.flow

    override suspend fun getChildrenDocumentName(path: String): List<String> {
        val documentNameList = api.getChildrenDocumentName(path)
        return documentNameList.documents?.map {
            it.name.split("/").last()
        } ?: emptyList()
    }

    override suspend fun deleteDocument(path: String) {
        api.deleteDocument(path)
    }

    override suspend fun getSharedRoutineDays(routineId: String): List<SharedRoutineDay> {
        val sharedRoutineDays = mutableListOf<SharedRoutineDay>()
        api.getSharedRoutineDays(routineId)?.documents?.forEach() {
            sharedRoutineDays.add(it.toSharedRoutineDay())
        }
        return sharedRoutineDays
    }

    override suspend fun getSharedRoutineExercises(
        routineId: String,
        dayId: String
    ): List<SharedRoutineExercise> {
        val sharedRoutineExercises = mutableListOf<SharedRoutineExercise>()
        api.getSharedRoutineExercises(routineId, dayId)?.documents?.forEach() {
            sharedRoutineExercises.add(it.toSharedRoutineExercise())
        }
        return sharedRoutineExercises
    }

    override suspend fun getSharedRoutineExerciseSets(
        routineId: String,
        dayId: String,
        exerciseId: String
    ): List<SharedRoutineExerciseSet> {
        val sharedRoutineExerciseSets = mutableListOf<SharedRoutineExerciseSet>()
        api.getSharedRoutineExerciseSets(routineId, dayId, exerciseId)?.documents?.forEach() {
            sharedRoutineExerciseSets.add(it.toSharedRoutineExerciseSet())
        }
        return sharedRoutineExerciseSets
    }

    override suspend fun commitTransaction(writes: List<WriteModelData>) {
        api.commitTransaction(WriteRequestBody(writes))
    }
}