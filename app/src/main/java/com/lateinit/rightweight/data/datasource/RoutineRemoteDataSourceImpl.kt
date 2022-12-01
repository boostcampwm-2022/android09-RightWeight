package com.lateinit.rightweight.data.datasource

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