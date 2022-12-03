package com.lateinit.rightweight.data.datasource

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.lateinit.rightweight.data.RoutineApiService
import com.lateinit.rightweight.data.database.AppDatabase
import com.lateinit.rightweight.data.database.AppSharedPreferences
import com.lateinit.rightweight.data.database.mediator.SharedRoutineRemoteMediator
import javax.inject.Inject

class RoutineRemoteDataSourceImpl @Inject constructor(
    private val db: AppDatabase,
    private val api: RoutineApiService,
    private val appSharedPreferences: AppSharedPreferences
): RoutineRemoteDataSource {


    @OptIn(ExperimentalPagingApi::class)
    override fun getSharedRoutinesByPaging() = Pager(
        config = PagingConfig(10),
        remoteMediator = SharedRoutineRemoteMediator(
            db, api, appSharedPreferences
        ),
    ) {
        db.sharedRoutineDao().getAllSharedRoutinesByPaging()
    }.flow
import android.util.Log
import com.google.gson.Gson
import com.lateinit.rightweight.data.RoutineApiService
import com.lateinit.rightweight.data.model.RootField
import javax.inject.Inject

class RoutineRemoteDataSourceImpl @Inject constructor(
    private val routineApiService: RoutineApiService
) : RoutineRemoteDataSource {
    override suspend fun shareRoutine(routineId: String, rootField: RootField) {
        routineApiService.shareRoutine(routineId, rootField)
    }

    override suspend fun shareDay(routineId: String, dayId: String, rootField: RootField) {
        routineApiService.shareRoutineDay(routineId, dayId, rootField)
    }

    override suspend fun shareExercise(
        routineId: String,
        dayId: String,
        exerciseId: String,
        rootField: RootField
    ) {
        routineApiService.shareRoutineExercise(routineId, dayId, exerciseId, rootField)
    }

    override suspend fun shareExerciseSet(
        routineId: String,
        dayId: String,
        exerciseId: String,
        exerciseSetId: String,
        rootField: RootField
    ) {
        Log.d("RoutineExerciseAdapter", Gson().toJson(rootField))
        routineApiService.shareRoutineExerciseSet(
            routineId,
            dayId,
            exerciseId,
            exerciseSetId,
            rootField
        )
    }
}