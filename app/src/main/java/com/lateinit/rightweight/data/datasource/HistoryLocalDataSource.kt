package com.lateinit.rightweight.data.datasource

import android.util.Log
import androidx.sqlite.db.SimpleSQLiteQuery
import com.lateinit.rightweight.data.ExercisePartType
import com.lateinit.rightweight.data.database.dao.HistoryDao
import com.lateinit.rightweight.data.database.entity.Day
import com.lateinit.rightweight.data.database.entity.Exercise
import com.lateinit.rightweight.data.database.entity.ExerciseSet
import com.lateinit.rightweight.data.database.entity.History
import com.lateinit.rightweight.data.database.entity.HistoryExercise
import com.lateinit.rightweight.data.database.entity.HistorySet
import com.lateinit.rightweight.data.database.intermediate.HistoryWithHistoryExercises
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.util.UUID
import javax.inject.Inject

class HistoryLocalDataSource @Inject constructor(
    private val historyDao: HistoryDao
): HistoryDataSource {

    override suspend fun loadHistoryByDate(localDate: LocalDate): Flow<List<History>> {
        return historyDao.loadHistoryByDate(localDate)
    }

    override fun getHistoryBetweenDate(
        startDate: LocalDate,
        endDate: LocalDate
    ): Flow<List<HistoryWithHistoryExercises>> {
        return historyDao.getHistoryBetweenDate(startDate, endDate)
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

    override suspend fun getHistoryExercisesByHistoryId(historyId: String): Flow<List<HistoryExercise>> {
        return historyDao.getHistoryExercisesByHistoryId(historyId)
    }

    override suspend fun getHistorySetsByHistoryExerciseId(exerciseId: String): Flow<List<HistorySet>> {
        return historyDao.getHistorySetsByHistoryExerciseId(exerciseId)
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

    override suspend fun verifyAllHistorySets(historyExercises: List<HistoryExercise>): Boolean {
        return historyDao.verifyAllHistorySets(verifyAllHistorySetsQuery(historyExercises))
    }

    fun verifyAllHistorySetsQuery(historyExercises: List<HistoryExercise>): SimpleSQLiteQuery {
        val selectQuery = "SELECT COALESCE(MIN(`checked`), 1) FROM history_set"

        val finalQuery =
            selectQuery + historyExercises.joinToString(prefix = " WHERE (", separator = " OR ") {
                "exercise_id = '${it.exerciseId}'"
            } + ")"

        Log.d("finalQuery", finalQuery)
        return SimpleSQLiteQuery(finalQuery)
    }

}