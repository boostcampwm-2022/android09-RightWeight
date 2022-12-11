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
import com.lateinit.rightweight.ui.mapper.toHistoryUiModel
import com.lateinit.rightweight.ui.model.history.HistoryExerciseSetUiModel
import com.lateinit.rightweight.ui.model.history.HistoryExerciseUiModel
import com.lateinit.rightweight.ui.model.history.HistoryUiModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject

class HistoryRepositoryImpl @Inject constructor(
    private val historyLocalDataSource: HistoryLocalDataSource,
    private val historyRemoteDatasource: HistoryRemoteDatasource
): HistoryRepository {

    override suspend fun saveHistory(
        routineId: String,
        day: Day,
        routineTitle: String,
        exercises: List<Exercise>,
        exerciseSets: List<ExerciseSet>
    ) {
        return  historyLocalDataSource.insertHistory(routineId, day, routineTitle, exercises, exerciseSets)
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

    override suspend fun getHistoryAfterDate(startDate: LocalDate): List<HistoryUiModel> {
        return historyLocalDataSource.getHistoryAfterDate(startDate).map { it.toHistoryUiModel() }
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
    ): Flow<Map<LocalDate, HistoryUiModel>> {
        return historyLocalDataSource.getHistoryBetweenDate(startDate, endDate).map {
            it.associate { historyWithExercise ->
                historyWithExercise.history.date to historyWithExercise.toHistoryUiModel()
            }
        }
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

    override suspend fun removeAllHistories() {
        historyLocalDataSource.removeAllHistories()
    }
}