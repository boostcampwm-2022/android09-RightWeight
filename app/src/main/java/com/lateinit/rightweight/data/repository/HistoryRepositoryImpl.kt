package com.lateinit.rightweight.data.repository

import com.lateinit.rightweight.data.database.entity.Day
import com.lateinit.rightweight.data.database.entity.Exercise
import com.lateinit.rightweight.data.database.entity.ExerciseSet
import com.lateinit.rightweight.data.database.entity.History
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

}