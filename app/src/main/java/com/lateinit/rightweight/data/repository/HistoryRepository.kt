package com.lateinit.rightweight.data.repository

import com.lateinit.rightweight.data.database.entity.Day
import com.lateinit.rightweight.data.database.entity.Exercise
import com.lateinit.rightweight.data.database.entity.ExerciseSet
import com.lateinit.rightweight.data.database.entity.History
import com.lateinit.rightweight.data.database.intermediate.HistoryWithHistoryExercises
import com.lateinit.rightweight.ui.model.HistoryExerciseSetUiModel
import com.lateinit.rightweight.ui.model.HistoryExerciseUiModel
import com.lateinit.rightweight.ui.model.HistoryUiModel
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface HistoryRepository {

    suspend fun saveHistory(
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

    suspend fun updateHistory(historyUiModel: HistoryUiModel)

    suspend fun updateHistorySet(historyExerciseSetUiModel: HistoryExerciseSetUiModel)

    suspend fun updateHistoryExercise(historyExerciseUiModel: HistoryExerciseUiModel)

    suspend fun removeHistorySet(historySetId: String)

    suspend fun removeHistoryExercise(historyExerciseId: String)
}