package com.lateinit.rightweight.data.datasource

import com.lateinit.rightweight.data.ExercisePartType
import com.lateinit.rightweight.data.database.dao.HistoryDao
import com.lateinit.rightweight.data.database.entity.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*
import javax.inject.Inject

class HistoryLocalDataSource @Inject constructor(
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

    override suspend fun getHistoryExercisesByHistoryId(historyId: String): List<HistoryExercise> {
        return historyDao.getHistoryExercisesByHistoryId(historyId)
    }

    override suspend fun getHistorySetsByHistoryExerciseId(exerciseId: String): List<HistorySet> {
        return historyDao.getHistorySetsByHistoryExerciseId(exerciseId)
    }

    override suspend fun updateHistorySet(historySet: HistorySet) {
        historyDao.updatetHistorySet(historySet)
    }

    override suspend fun updateHistoryExercise(historyExercise: HistoryExercise) {
        historyDao.updateHistoryExercise(historyExercise)
    }

    override suspend fun removeHistorySet(historySetId: String) {
        historyDao.removeHistorySet(historySetId)
    }

    override suspend fun removeHistoryExercise(historyExerciseId: String) {
        historyDao.removeHistoryExercise(historyExerciseId)
    }

    override suspend fun addHistorySet(historyExerciseId: String) {
        val historySetId = UUID.randomUUID().toString()
        val maxHistorySetOrder = historyDao.getMaxHistorySetOrder()
        val newHistorySet = HistorySet(historySetId, historyExerciseId, "", "", maxHistorySetOrder + 1, false)
        historyDao.insertHistorySet(newHistorySet)
    }

    override suspend fun addHistoryExercise(historyId: String) {
        val historyExerciseId = UUID.randomUUID().toString()
        val maxHistoryExerciseOrder = historyDao.getMaxHistoryExerciseOrder()
        val newHistoryExercise = HistoryExercise(historyExerciseId, historyId, "", maxHistoryExerciseOrder + 1, ExercisePartType.CHEST)
        historyDao.insertHistoryExercise(newHistoryExercise)
    }
}