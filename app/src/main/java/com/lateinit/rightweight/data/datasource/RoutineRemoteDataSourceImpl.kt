package com.lateinit.rightweight.data.datasource

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.lateinit.rightweight.data.RoutineApiService
import com.lateinit.rightweight.data.database.AppDatabase
import com.lateinit.rightweight.data.database.AppSharedPreferences
import com.lateinit.rightweight.data.database.mediator.SharedRoutineRemoteMediator
import com.lateinit.rightweight.data.remote.model.RootField
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

}