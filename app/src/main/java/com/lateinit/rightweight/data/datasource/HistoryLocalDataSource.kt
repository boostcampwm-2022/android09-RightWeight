package com.lateinit.rightweight.data.datasource

import com.lateinit.rightweight.data.ExercisePartType
import com.lateinit.rightweight.data.database.dao.HistoryDao
import com.lateinit.rightweight.data.database.entity.*
import com.lateinit.rightweight.data.database.intermediate.HistoryWithHistoryExercises
import com.lateinit.rightweight.util.createRandomUUID
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import javax.inject.Inject

class HistoryLocalDataSource @Inject constructor(
    private val historyDao: HistoryDao
): HistoryDataSource {

    override suspend fun saveHistory(
        routineId: String,
        day: Day,
        exercises: List<Exercise>,
        exerciseSets: List<ExerciseSet>
    ) {
        val historyId = createRandomUUID()
        val history = History(historyId, LocalDate.now(), "00:00:00", "", day.order, false, routineId)
        val historyExercises = mutableListOf<HistoryExercise>()
        val historySets = mutableListOf<HistorySet>()
        for(exercise in exercises){
            val historyExerciseId = createRandomUUID()
            val historyExercise = HistoryExercise(historyExerciseId, historyId, exercise.title, exercise.order, exercise.part)
            historyExercises.add(historyExercise)
            for(exerciseSet in exerciseSets){
                if(exerciseSet.exerciseId == exercise.exerciseId){
                    val historySetId = createRandomUUID()
                    val historySet = HistorySet(historySetId, historyExerciseId, exerciseSet.weight, exerciseSet.count, exerciseSet.order, false)
                    historySets.add(historySet)
                }
            }
        }

        historyDao.insertHistory(history, historyExercises, historySets)
    }

    override suspend fun insertHistorySet(historyExerciseId: String) {
        val historySetId = createRandomUUID()
        val maxHistorySetOrder = historyDao.getMaxHistorySetOrder()
        val newHistorySet = HistorySet(
            setId = historySetId,
            exerciseId = historyExerciseId,
            weight = "",
            count = "",
            order = maxHistorySetOrder.inc(),
            checked = false
        )
        historyDao.insertHistorySet(newHistorySet)
    }

    override suspend fun insertHistoryExercise(historyId: String) {
        val historyExerciseId = createRandomUUID()
        val maxHistoryExerciseOrder = historyDao.getMaxHistoryExerciseOrder()
        val newHistoryExercise = HistoryExercise(
            exerciseId = historyExerciseId,
            historyId = historyId,
            title = "",
            order = maxHistoryExerciseOrder.inc(),
            part = ExercisePartType.CHEST
        )
        historyDao.insertHistoryExercise(newHistoryExercise)
    }

    override fun loadHistoryByDate(localDate: LocalDate): Flow<History> {
        return historyDao.loadHistoryByDate(localDate)
    }

    override fun getHistoryByDate(localDate: LocalDate): Flow<HistoryWithHistoryExercises?> {
        return historyDao.getHistoryByDate(localDate)
    }

    override fun getHistoryBetweenDate(
        startDate: LocalDate,
        endDate: LocalDate
    ): Flow<List<HistoryWithHistoryExercises>> {
        return historyDao.getHistoryBetweenDate(startDate, endDate)
    }

    override suspend fun updateHistory(history: History) {
        historyDao.updateHistory(history)
    }

    override suspend fun updateHistorySet(historySet: HistorySet) {
        historyDao.updateHistorySet(historySet)
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
}