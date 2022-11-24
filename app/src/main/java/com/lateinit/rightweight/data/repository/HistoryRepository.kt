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

    suspend fun saveHistorySet(historySet: HistorySet)
    suspend fun removeHistorySet(historySetId: String)
    suspend fun addHistorySet(historyExerciseId: String)
}