package com.lateinit.rightweight.data.repository

import com.lateinit.rightweight.data.database.entity.Day
import com.lateinit.rightweight.data.database.entity.Exercise
import com.lateinit.rightweight.data.database.entity.ExerciseSet
import com.lateinit.rightweight.data.database.entity.History
import com.lateinit.rightweight.data.database.intermediate.HistoryWithHistoryExercises
import com.lateinit.rightweight.ui.model.history.HistoryExerciseSetUiModel
import com.lateinit.rightweight.ui.model.history.HistoryExerciseUiModel
import com.lateinit.rightweight.ui.model.history.HistoryUiModel
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface HistoryRepository {

    suspend fun saveHistory(
        routineId: String,
        day: Day,
        routineTitle: String,
        exercises: List<Exercise>,
        exerciseSets: List<ExerciseSet>
    )

    suspend fun insertHistorySet(historyExerciseId: String)

    suspend fun insertHistoryExercise(historyId: String)

    fun getHistoryByDate(localDate: LocalDate): Flow<History>

    fun getHistoryWithHistoryExercisesByDate(localDate: LocalDate): Flow<HistoryWithHistoryExercises?>

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