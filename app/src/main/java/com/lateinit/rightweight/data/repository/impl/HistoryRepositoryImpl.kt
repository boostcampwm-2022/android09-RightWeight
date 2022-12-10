package com.lateinit.rightweight.data.repository.impl

import com.lateinit.rightweight.data.database.entity.Day
import com.lateinit.rightweight.data.database.entity.Exercise
import com.lateinit.rightweight.data.database.entity.ExerciseSet
import com.lateinit.rightweight.data.database.entity.History
import com.lateinit.rightweight.data.database.intermediate.HistoryWithHistoryExercises
import com.lateinit.rightweight.data.datasource.local.HistoryLocalDataSource
import com.lateinit.rightweight.data.datasource.remote.HistoryRemoteDatasource
import com.lateinit.rightweight.data.mapper.toHistory
import com.lateinit.rightweight.data.mapper.toHistoryExercise
import com.lateinit.rightweight.data.mapper.toHistorySet
import com.lateinit.rightweight.data.repository.HistoryRepository
import com.lateinit.rightweight.ui.model.history.HistoryExerciseSetUiModel
import com.lateinit.rightweight.ui.model.history.HistoryExerciseUiModel
import com.lateinit.rightweight.ui.model.history.HistoryUiModel
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import javax.inject.Inject

class HistoryRepositoryImpl @Inject constructor(
    private val historyLocalDataSource: HistoryLocalDataSource,
    private val historyRemoteDatasource: HistoryRemoteDatasource
): HistoryRepository {

    override suspend fun saveHistory(
        routineId: String,
        day: Day,
        exercises: List<Exercise>,

        exerciseSets: List<ExerciseSet>
    ) {
        return  historyLocalDataSource.insertHistory(routineId, day, exercises, exerciseSets)
    }

    override suspend fun insertHistorySet(historyExerciseId: String) {
        historyLocalDataSource.insertHistorySet(historyExerciseId)
    }

    override suspend fun insertHistoryExercise(historyId: String) {
        historyLocalDataSource.insertHistoryExercise(historyId)
    }

    override suspend fun getLatestHistoryDate(userId: String): LocalDate {
        return historyRemoteDatasource.getLatestHistoryDate(userId)
    }

    override fun getHistoryByDate(localDate: LocalDate): Flow<History> {
        return  historyLocalDataSource.getHistoryByDate(localDate)
    }

    override fun getHistoryWithHistoryExercisesByDate(localDate: LocalDate): Flow<HistoryWithHistoryExercises?> {
        return historyLocalDataSource.getHistoryWithHistoryExercisesByDate(localDate)
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