package com.lateinit.rightweight.data.datasource

import com.lateinit.rightweight.data.database.AppPreferencesDataStore
import com.lateinit.rightweight.data.database.dao.UserDao
import com.lateinit.rightweight.data.database.intermediate.HistoryWithHistoryExercises
import com.lateinit.rightweight.data.database.intermediate.RoutineWithDays
import com.lateinit.rightweight.data.model.User
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import javax.inject.Inject

class UserLocalDataSourceImpl @Inject constructor(
    private val userDao: UserDao,
    private val appPreferencesDataStore: AppPreferencesDataStore
) : UserLocalDataSource {

    override suspend fun saveUser(user: User) {
        appPreferencesDataStore.saveUser(user)
    }

    override fun getUser(): Flow<User?> {
        return appPreferencesDataStore.userInfo
    }

    override suspend fun getAllRoutineWithDays(): List<RoutineWithDays> {
        return userDao.getAllRoutineWithDays()
    }

    override suspend fun getHistoryAfterDate(startDate: LocalDate): List<HistoryWithHistoryExercises> {
        return userDao.getHistoryAfterDate(startDate)
    }
}