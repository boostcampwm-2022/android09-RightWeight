package com.lateinit.rightweight.data.datasource

import com.lateinit.rightweight.data.database.dao.HistoryDao
import com.lateinit.rightweight.data.database.entity.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*
import javax.inject.Inject

class HistoryLocalDataSource@Inject constructor(
    private val historyDao: HistoryDao
): HistoryDataSource {
    override suspend fun loadHistoryByDate(localDate: LocalDate): List<History> {
        return historyDao.loadHistoryByDate(localDate)
    }

    override suspend fun saveHistory(
        day: Day,
        exercises: List<Exercise>,
        exerciseSets: List<ExerciseSet>
    ) {
        val historyId = UUID.randomUUID().toString()
        val history = History(historyId, LocalDate.now(), "00:00:00", "", day.order, false)
        val historyExercises = mutableListOf<HistoryExercise>()
        val historySets = mutableListOf<HistorySet>()
        for(exercise in exercises){
            val historyExerciseId = UUID.randomUUID().toString()
            val historyExercise = HistoryExercise(historyExerciseId, historyId, exercise.title, exercise.order, exercise.part)
            historyExercises.add(historyExercise)
            for(exerciseSet in exerciseSets){
                if(exerciseSet.exerciseId == exercise.exerciseId){
                    val historySetId = UUID.randomUUID().toString()
                    val historySet = HistorySet(historySetId, historyExerciseId, exerciseSet.weight, exerciseSet.count, exerciseSet.order, false)
                    historySets.add(historySet)
                }
            }
        }

        historyDao.insertHistory(history, historyExercises, historySets)
    }
}