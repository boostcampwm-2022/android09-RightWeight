package com.lateinit.rightweight.data.repository

import com.lateinit.rightweight.data.database.entity.*
import java.time.LocalDate

interface HistoryRepository {

    suspend fun loadHistoryByDate(localDate: LocalDate): List<History>

    suspend fun saveHistory(
        day: Day,
        exercises: List<Exercise>,
        exerciseSets: List<ExerciseSet>
    )

    suspend fun getHistoryExercisesByHistoryId(historyId: String): List<HistoryExercise>
    suspend fun getHistorySetsByHistoryExerciseId(exerciseId: String): List<HistorySet>

    suspend fun updateHistorySet(historySet: HistorySet)
    suspend fun updateHistoryExercise(historyExercise: HistoryExercise)
    suspend fun removeHistorySet(historySetId: String)
    suspend fun removeHistoryExercise(historyExerciseId: String)
    suspend fun addHistorySet(historyExerciseId: String)
    suspend fun addHistoryExercise(historyId: String)
}