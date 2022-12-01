package com.lateinit.rightweight.data.repository

import com.lateinit.rightweight.data.database.entity.Day
import com.lateinit.rightweight.data.database.entity.Exercise
import com.lateinit.rightweight.data.database.entity.ExerciseSet
import com.lateinit.rightweight.data.database.entity.History
import com.lateinit.rightweight.data.database.entity.HistoryExercise
import com.lateinit.rightweight.data.database.entity.HistorySet
import com.lateinit.rightweight.data.database.intermediate.HistoryWithHistoryExercises
import com.lateinit.rightweight.data.datasource.HistoryLocalDataSource
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import javax.inject.Inject

class HistoryRepositoryImpl @Inject constructor(
    private val historyLocalDataSource: HistoryLocalDataSource
): HistoryRepository {

    override suspend fun loadHistoryByDate(localDate: LocalDate): Flow<List<History>> {
        return  historyLocalDataSource.loadHistoryByDate(localDate)
    }

    override fun getHistoryBetweenDate(
        startDate: LocalDate,
        endDate: LocalDate
    ): Flow<List<HistoryWithHistoryExercises>> {
        return historyLocalDataSource.getHistoryBetweenDate(startDate, endDate)
    }

    override suspend fun saveHistory(
        day: Day,
        exercises: List<Exercise>,
        exerciseSets: List<ExerciseSet>
    ) {
        return  historyLocalDataSource.saveHistory(day, exercises, exerciseSets)
    }

    override suspend fun getHistoryExercisesByHistoryId(historyId: String): Flow<List<HistoryExercise>> {
        return historyLocalDataSource.getHistoryExercisesByHistoryId(historyId)
    }

    override suspend fun getHistorySetsByHistoryExerciseId(exerciseId: String): Flow<List<HistorySet>> {
        return historyLocalDataSource.getHistorySetsByHistoryExerciseId(exerciseId)
    }

    override suspend fun updateHistory(history: History) {
        historyLocalDataSource.updateHistory(history)
    }

    override suspend fun updateHistorySet(historySet: HistorySet) {
        historyLocalDataSource.updateHistorySet(historySet)
    }

    override suspend fun updateHistoryExercise(historyExercise: HistoryExercise) {
        historyLocalDataSource.updateHistoryExercise(historyExercise)
    }

    override suspend fun removeHistorySet(historySetId: String) {
        historyLocalDataSource.removeHistorySet(historySetId)
    }

    override suspend fun removeHistoryExercise(historyExerciseId: String) {
        historyLocalDataSource.removeHistoryExercise(historyExerciseId)
    }

    override suspend fun addHistorySet(historyExerciseId: String) {
        historyLocalDataSource.addHistorySet(historyExerciseId)
    }

    override suspend fun addHistoryExercise(historyId: String) {
        historyLocalDataSource.addHistoryExercise(historyId)
    }

    override suspend fun verifyAllHistorySets(historyExercises: List<HistoryExercise>): Boolean {
        return historyLocalDataSource.verifyAllHistorySets(historyExercises)
    }
}