package com.lateinit.rightweight.data.datasource.local.impl

import com.lateinit.rightweight.data.database.dao.HistoryDao
import com.lateinit.rightweight.data.database.entity.Day
import com.lateinit.rightweight.data.database.entity.Exercise
import com.lateinit.rightweight.data.database.entity.ExerciseSet
import com.lateinit.rightweight.data.database.entity.History
import com.lateinit.rightweight.data.database.entity.HistoryExercise
import com.lateinit.rightweight.data.database.entity.HistorySet
import com.lateinit.rightweight.data.database.intermediate.HistoryWithHistoryExercises
import com.lateinit.rightweight.data.datasource.local.HistoryLocalDataSource
import com.lateinit.rightweight.data.model.local.ExercisePartType
import com.lateinit.rightweight.util.createRandomUUID
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import javax.inject.Inject

class HistoryLocalDataSourceImpl @Inject constructor(
    private val historyDao: HistoryDao
): HistoryLocalDataSource {

    override suspend fun insertHistory(
        routineId: String,
        day: Day,
        routineTitle: String,
        exercises: List<Exercise>,
        exerciseSets: List<ExerciseSet>
    ) {
        val historyId = createRandomUUID()
        val history = History(historyId, LocalDate.now(), "00:00:00", routineTitle, day.order, false, routineId)
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

    override suspend fun getHistoryAfterDate(startDate: LocalDate): List<HistoryWithHistoryExercises> {
        return historyDao.getHistoryAfterDate(startDate)
    }

    override fun getHistoryByDate(localDate: LocalDate): Flow<History> {
        return historyDao.getHistoryByDate(localDate)
    }

    override fun getHistoryWithHistoryExercisesByDate(localDate: LocalDate): Flow<HistoryWithHistoryExercises?> {
        return historyDao.getHistoryWithHistoryExercisesByDate(localDate)
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