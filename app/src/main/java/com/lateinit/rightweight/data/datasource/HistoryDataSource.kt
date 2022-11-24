package com.lateinit.rightweight.data.datasource

import com.lateinit.rightweight.data.database.entity.Day
import com.lateinit.rightweight.data.database.entity.Exercise
import com.lateinit.rightweight.data.database.entity.ExerciseSet
import com.lateinit.rightweight.data.database.entity.History
import java.time.LocalDate

interface HistoryDataSource {

    suspend fun loadHistoryByDate(localDate: LocalDate): List<History>

    suspend fun saveHistory(
        day: Day,
        exercises: List<Exercise>,
        exerciseSets: List<ExerciseSet>
    )

}