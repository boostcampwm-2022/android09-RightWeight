package com.lateinit.rightweight.data.repository

import com.lateinit.rightweight.data.database.entity.*
import com.lateinit.rightweight.data.datasource.HistoryLocalDataSource
import java.time.LocalDate
import javax.inject.Inject

class HistoryRepositoryImpl @Inject constructor(
    private val historyLocalDataSource: HistoryLocalDataSource
): HistoryRepository {

    override suspend fun loadHistoryByDate(localDate: LocalDate): List<History> {
        return  historyLocalDataSource.loadHistoryByDate(localDate)
    }

    override suspend fun saveHistory(
        day: Day,
        exercises: List<Exercise>,
        exerciseSets: List<ExerciseSet>
    ) {
        return  historyLocalDataSource.saveHistory(day, exercises, exerciseSets)
    }

    override suspend fun getHistoryExercisesByHistoryId(historyId: String): List<HistoryExercise> {
        return historyLocalDataSource.getHistoryExercisesByHistoryId(historyId)
    }

    override suspend fun getHistorySetsByHistoryExerciseId(exerciseId: String): List<HistorySet> {
        return historyLocalDataSource.getHistorySetsByHistoryExerciseId(exerciseId)
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

}