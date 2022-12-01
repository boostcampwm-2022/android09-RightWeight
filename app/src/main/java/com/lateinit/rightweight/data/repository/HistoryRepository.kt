package com.lateinit.rightweight.data.repository

import com.lateinit.rightweight.data.database.entity.Day
import com.lateinit.rightweight.data.database.entity.Exercise
import com.lateinit.rightweight.data.database.entity.ExerciseSet
import com.lateinit.rightweight.data.database.entity.History
import com.lateinit.rightweight.data.database.entity.HistoryExercise
import com.lateinit.rightweight.data.database.entity.HistorySet
import com.lateinit.rightweight.data.database.intermediate.HistoryWithHistoryExercises
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface HistoryRepository {

    suspend fun loadHistoryByDate(localDate: LocalDate): Flow<List<History>>

    fun getHistoryBetweenDate(
        startDate: LocalDate,
        endDate: LocalDate
    ): Flow<List<HistoryWithHistoryExercises>>

    suspend fun saveHistory(
        day: Day,
        exercises: List<Exercise>,
        exerciseSets: List<ExerciseSet>
    )

    suspend fun getHistoryExercisesByHistoryId(historyId: String): Flow<List<HistoryExercise>>
    suspend fun getHistorySetsByHistoryExerciseId(exerciseId: String): Flow<List<HistorySet>>

    suspend fun updateHistory(history: History)
    suspend fun updateHistorySet(historySet: HistorySet)
    suspend fun updateHistoryExercise(historyExercise: HistoryExercise)
    suspend fun removeHistorySet(historySetId: String)
    suspend fun removeHistoryExercise(historyExerciseId: String)
    suspend fun addHistorySet(historyExerciseId: String)
    suspend fun addHistoryExercise(historyId: String)
    suspend fun verifyAllHistorySets(historyExercises: List<HistoryExercise>): Boolean
}