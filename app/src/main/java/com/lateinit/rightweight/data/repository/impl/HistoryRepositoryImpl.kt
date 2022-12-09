package com.lateinit.rightweight.data.repository.impl

import com.lateinit.rightweight.data.database.entity.Day
import com.lateinit.rightweight.data.database.entity.Exercise
import com.lateinit.rightweight.data.database.entity.ExerciseSet
import com.lateinit.rightweight.data.database.entity.History
import com.lateinit.rightweight.data.database.intermediate.HistoryWithHistoryExercises
import com.lateinit.rightweight.data.datasource.local.impl.HistoryLocalLocalDataSourceImpl
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
    private val historyLocalDataSourceImpl: HistoryLocalLocalDataSourceImpl
): HistoryRepository {

    override suspend fun saveHistory(
        routineId: String,
        day: Day,
        exercises: List<Exercise>,
        exerciseSets: List<ExerciseSet>
    ) {
        return  historyLocalDataSourceImpl.insertHistory(routineId, day, exercises, exerciseSets)
    }

    override suspend fun insertHistorySet(historyExerciseId: String) {
        historyLocalDataSourceImpl.insertHistorySet(historyExerciseId)
    }

    override suspend fun insertHistoryExercise(historyId: String) {
        historyLocalDataSourceImpl.insertHistoryExercise(historyId)
    }

    override fun getHistoryByDate(localDate: LocalDate): Flow<History> {
        return  historyLocalDataSourceImpl.getHistoryByDate(localDate)
    }

    override fun getHistoryWithHistoryExercisesByDate(localDate: LocalDate): Flow<HistoryWithHistoryExercises?> {
        return historyLocalDataSourceImpl.getHistoryWithHistoryExercisesByDate(localDate)
    }

    override fun getHistoryBetweenDate(
        startDate: LocalDate,
        endDate: LocalDate
    ): Flow<List<HistoryWithHistoryExercises>> {
        return historyLocalDataSourceImpl.getHistoryBetweenDate(startDate, endDate)
    }

    override suspend fun updateHistory(historyUiModel: HistoryUiModel) {
        historyLocalDataSourceImpl.updateHistory(historyUiModel.toHistory())
    }

    override suspend fun updateHistorySet(historyExerciseSetUiModel: HistoryExerciseSetUiModel) {
        historyLocalDataSourceImpl.updateHistorySet(historyExerciseSetUiModel.toHistorySet())
    }

    override suspend fun updateHistoryExercise(historyExerciseUiModel: HistoryExerciseUiModel) {
        historyLocalDataSourceImpl.updateHistoryExercise(historyExerciseUiModel.toHistoryExercise())
    }

    override suspend fun removeHistorySet(historySetId: String) {
        historyLocalDataSourceImpl.removeHistorySet(historySetId)
    }

    override suspend fun removeHistoryExercise(historyExerciseId: String) {
        historyLocalDataSourceImpl.removeHistoryExercise(historyExerciseId)
    }
}