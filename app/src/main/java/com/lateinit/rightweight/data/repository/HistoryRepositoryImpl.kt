package com.lateinit.rightweight.data.repository

import com.lateinit.rightweight.data.database.entity.Day
import com.lateinit.rightweight.data.database.entity.Exercise
import com.lateinit.rightweight.data.database.entity.ExerciseSet
import com.lateinit.rightweight.data.database.entity.History
import com.lateinit.rightweight.data.database.intermediate.HistoryWithHistoryExercises
import com.lateinit.rightweight.data.datasource.HistoryLocalDataSource
import com.lateinit.rightweight.ui.model.HistoryExerciseSetUiModel
import com.lateinit.rightweight.ui.model.HistoryExerciseUiModel
import com.lateinit.rightweight.ui.model.HistoryUiModel
import com.lateinit.rightweight.util.toHistory
import com.lateinit.rightweight.util.toHistoryExercise
import com.lateinit.rightweight.util.toHistorySet
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import javax.inject.Inject

class HistoryRepositoryImpl @Inject constructor(
    private val historyLocalDataSource: HistoryLocalDataSource
): HistoryRepository {

    override suspend fun saveHistory(
        routineId: String,
        day: Day,
        exercises: List<Exercise>,
        exerciseSets: List<ExerciseSet>
    ) {
        return  historyLocalDataSource.saveHistory(routineId, day, exercises, exerciseSets)
    }

    override suspend fun insertHistorySet(historyExerciseId: String) {
        historyLocalDataSource.insertHistorySet(historyExerciseId)
    }

    override suspend fun insertHistoryExercise(historyId: String) {
        historyLocalDataSource.insertHistoryExercise(historyId)
    }

    override fun loadHistoryByDate(localDate: LocalDate): Flow<History> {
        return  historyLocalDataSource.loadHistoryByDate(localDate)
    }

    override fun getHistoryByDate(localDate: LocalDate): Flow<HistoryWithHistoryExercises?> {
        return historyLocalDataSource.getHistoryByDate(localDate)
    }

    override fun getHistoryBetweenDate(
        startDate: LocalDate,
        endDate: LocalDate
    ): Flow<List<HistoryWithHistoryExercises>> {
        return historyLocalDataSource.getHistoryBetweenDate(startDate, endDate)
    }

    override suspend fun updateHistory(historyUiModel: HistoryUiModel) {
        historyLocalDataSource.updateHistory(historyUiModel.toHistory())
    }

    override suspend fun updateHistorySet(historyExerciseSetUiModel: HistoryExerciseSetUiModel) {
        historyLocalDataSource.updateHistorySet(historyExerciseSetUiModel.toHistorySet())
    }

    override suspend fun updateHistoryExercise(historyExerciseUiModel: HistoryExerciseUiModel) {
        historyLocalDataSource.updateHistoryExercise(historyExerciseUiModel.toHistoryExercise())
    }

    override suspend fun removeHistorySet(historySetId: String) {
        historyLocalDataSource.removeHistorySet(historySetId)
    }

    override suspend fun removeHistoryExercise(historyExerciseId: String) {
        historyLocalDataSource.removeHistoryExercise(historyExerciseId)
    }
}