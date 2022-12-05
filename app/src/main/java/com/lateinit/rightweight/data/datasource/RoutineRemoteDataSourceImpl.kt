package com.lateinit.rightweight.data.datasource

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.lateinit.rightweight.data.RoutineApiService
import com.lateinit.rightweight.data.database.AppDatabase
import com.lateinit.rightweight.data.database.entity.SharedRoutineDay
import com.lateinit.rightweight.data.database.entity.SharedRoutineExercise
import com.lateinit.rightweight.data.database.entity.SharedRoutineExerciseSet
import com.lateinit.rightweight.data.database.mediator.SharedRoutineRemoteMediator
import com.lateinit.rightweight.data.remote.model.RootField
import com.lateinit.rightweight.util.toSharedRoutineDay
import com.lateinit.rightweight.util.toSharedRoutineExercise
import com.lateinit.rightweight.util.toSharedRoutineExerciseSet
import javax.inject.Inject

class RoutineRemoteDataSourceImpl @Inject constructor(
    private val db: AppDatabase,
    private val api: RoutineApiService,
    private val appSharedPreferences: AppSharedPreferences
) : RoutineRemoteDataSource {

    @OptIn(ExperimentalPagingApi::class)
    override fun getSharedRoutinesByPaging() = Pager(
        config = PagingConfig(10),
        remoteMediator = SharedRoutineRemoteMediator(
            db, api, appSharedPreferences
        ),
    ) {
        db.sharedRoutineDao().getAllSharedRoutinesByPaging()
    }.flow

    override suspend fun shareRoutine(routineId: String, rootField: RootField) {
        api.shareRoutine(routineId, rootField)
    }

    override suspend fun shareDay(routineId: String, dayId: String, rootField: RootField) {
        api.shareRoutineDay(routineId, dayId, rootField)
    }

    override suspend fun shareExercise(
        routineId: String,
        dayId: String,
        exerciseId: String,
        rootField: RootField
    ) {
        api.shareRoutineExercise(routineId, dayId, exerciseId, rootField)
    }

    override suspend fun shareExerciseSet(
        routineId: String,
        dayId: String,
        exerciseId: String,
        exerciseSetId: String,
        rootField: RootField
    ) {
        api.shareRoutineExerciseSet(
            routineId,
            dayId,
            exerciseId,
            exerciseSetId,
            rootField
        )
    }

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
        api.getSharedRoutineDays(routineId)?.documents?.forEach(){
            sharedRoutineDays.add(it.toSharedRoutineDay())
        }
        return sharedRoutineDays
    }

    override suspend fun getSharedRoutineExercises(
        routineId: String,
        dayId: String
    ): List<SharedRoutineExercise> {
        val sharedRoutineExercises = mutableListOf<SharedRoutineExercise>()
        api.getSharedRoutineExercises(routineId, dayId)?.documents?.forEach(){
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
        api.getSharedRoutineExerciseSets(routineId, dayId, exerciseId)?.documents?.forEach(){
            sharedRoutineExerciseSets.add(it.toSharedRoutineExerciseSet())
        }
        return sharedRoutineExerciseSets
    }
}