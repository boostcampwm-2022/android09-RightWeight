package com.lateinit.rightweight.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.lateinit.rightweight.data.database.dao.HistoryDao
import com.lateinit.rightweight.data.database.dao.RoutineDao
import com.lateinit.rightweight.data.database.entity.*

@Database(
    entities = [
        Routine::class, Day::class, Exercise::class, ExerciseSet::class,
        History::class, HistoryExercise::class, HistorySet::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun routineDao(): RoutineDao
    abstract fun historyDao(): HistoryDao
}