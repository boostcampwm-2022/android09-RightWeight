package com.lateinit.rightweight.data.datasource.remote.impl

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.lateinit.rightweight.data.api.RoutineApiService
import com.lateinit.rightweight.data.dataStore.AppPreferencesDataStore
import com.lateinit.rightweight.data.database.AppDatabase
import com.lateinit.rightweight.data.database.entity.SharedRoutineDay
import com.lateinit.rightweight.data.database.entity.SharedRoutineExercise
import com.lateinit.rightweight.data.database.entity.SharedRoutineExerciseSet
import com.lateinit.rightweight.data.datasource.remote.SharedRoutineRemoteDataSource
import com.lateinit.rightweight.data.mapper.toSharedRoutineDay
import com.lateinit.rightweight.data.mapper.toSharedRoutineExercise
import com.lateinit.rightweight.data.mapper.toSharedRoutineExerciseSet
import com.lateinit.rightweight.data.mediator.SharedRoutineRemoteMediator
import com.lateinit.rightweight.data.model.remote.SharedRoutineSortType
import com.lateinit.rightweight.data.model.remote.WriteModelData
import com.lateinit.rightweight.data.model.remote.WriteRequestBody
import com.lateinit.rightweight.data.remote.model.SharedRoutineField
import javax.inject.Inject

class SharedRoutineRemoteDataSourceImpl @Inject constructor(
    appPreferencesDataStore: AppPreferencesDataStore,
    private val db: AppDatabase,
    private val api: RoutineApiService
) : SharedRoutineRemoteDataSource {

    private val remoteMediator = SharedRoutineRemoteMediator(
        db, api, appPreferencesDataStore, SharedRoutineSortType.MODIFIED_DATE_FIRST
    )

    override suspend fun getSharedRoutine(routineId: String): SharedRoutineField? {
        val response = api.getSharedRoutine(routineId)
        return if (response.isSuccessful) {
            response.body()
        } else {
            null
        }
    }

    override suspend fun getSharedRoutineDays(routineId: String): List<SharedRoutineDay> {
        val sharedRoutineDays = mutableListOf<SharedRoutineDay>()
        api.getSharedRoutineDays(routineId)?.documents?.forEach {
            sharedRoutineDays.add(it.toSharedRoutineDay())
        }
        return sharedRoutineDays
    }

    override suspend fun getSharedRoutineExercises(
        routineId: String,
        dayId: String
    ): List<SharedRoutineExercise> {
        val sharedRoutineExercises = mutableListOf<SharedRoutineExercise>()
        api.getSharedRoutineExercises(routineId, dayId)?.documents?.forEach {
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
        api.getSharedRoutineExerciseSets(routineId, dayId, exerciseId)?.documents?.forEach {
            sharedRoutineExerciseSets.add(it.toSharedRoutineExerciseSet())
        }
        return sharedRoutineExerciseSets
    }

    @OptIn(ExperimentalPagingApi::class)
    override fun getSharedRoutinesByPaging() = Pager(
        config = PagingConfig(10, prefetchDistance = 0, initialLoadSize = 1),
        remoteMediator = remoteMediator,
    ) {
        db.sharedRoutineDao().getAllSharedRoutinesByPaging()
    }.flow

    override suspend fun setSharedRoutineSortType(sortType: SharedRoutineSortType) {
        remoteMediator.sortType = sortType
    }

    override suspend fun getChildrenDocumentName(path: String): List<String> {
        val documentNameList = api.getChildrenDocument(path)
        return documentNameList.documents?.map {
            it.name.split("/").last()
        } ?: emptyList()
    }

    override suspend fun commitTransaction(writes: List<WriteModelData>) {
        api.commitTransaction(WriteRequestBody(writes))
    }
}