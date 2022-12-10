package com.lateinit.rightweight.data.datasource.remote

import com.lateinit.rightweight.data.database.entity.Day
import com.lateinit.rightweight.data.database.entity.Exercise
import com.lateinit.rightweight.data.database.entity.ExerciseSet
import com.lateinit.rightweight.data.database.entity.Routine
import com.lateinit.rightweight.data.model.remote.DocumentResponse
import com.lateinit.rightweight.data.remote.model.RoutineField

interface RoutineRemoteDataSource {
    suspend fun getRoutineByUserId(userId: String): List<DocumentResponse<RoutineField>>

    suspend fun getRoutine(routineId: String, order: Int): Routine

    suspend fun getRoutineDays(routineId: String): List<Day>

    suspend fun getRoutineExercises(path: String): List<Exercise>

    suspend fun getRoutineExerciseSets(path: String): List<ExerciseSet>

    suspend fun getChildrenDocumentName(path: String): List<String>
}