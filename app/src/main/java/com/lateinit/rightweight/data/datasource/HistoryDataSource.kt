package com.lateinit.rightweight.data.datasource

import com.lateinit.rightweight.data.database.entity.Day
import com.lateinit.rightweight.data.database.entity.Exercise
import com.lateinit.rightweight.data.database.entity.ExerciseSet
import com.lateinit.rightweight.data.database.entity.History
import com.lateinit.rightweight.data.database.entity.HistoryExercise
import com.lateinit.rightweight.data.database.entity.HistorySet
import com.lateinit.rightweight.data.database.intermediate.HistoryWithHistoryExercises
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface HistoryDataSource {

    suspend fun saveHistory(
        routineId: String,
        day: Day,
        exercises: List<Exercise>,
        exerciseSets: List<ExerciseSet>
    )

    suspend fun insertHistorySet(historyExerciseId: String)

    suspend fun insertHistoryExercise(historyId: String)

    suspend fun loadHistoryByDate(localDate: LocalDate): Flow<List<History>>

    fun getHistoryByDate(localDate: LocalDate): Flow<HistoryWithHistoryExercises?>

    fun getHistoryBetweenDate(
        startDate: LocalDate,
        endDate: LocalDate
    ): Flow<List<HistoryWithHistoryExercises>>

    suspend fun updateHistory(history: History)

    suspend fun updateHistorySet(historySet: HistorySet)

    suspend fun updateHistoryExercise(historyExercise: HistoryExercise)

    suspend fun removeHistorySet(historySetId: String)

    suspend fun removeHistoryExercise(historyExerciseId: String)
}