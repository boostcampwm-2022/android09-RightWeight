package com.lateinit.rightweight.data.datasource

import com.lateinit.rightweight.data.database.AppPreferencesDataStore
import com.lateinit.rightweight.data.database.dao.UserDao
import com.lateinit.rightweight.data.database.intermediate.RoutineWithDays
import com.lateinit.rightweight.data.model.User
import com.lateinit.rightweight.ui.model.HistoryUiModel
import com.lateinit.rightweight.util.toHistoryUiModel
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

    override suspend fun getAllRoutineWithDays(): List<RoutineWithDays>{
        return userDao.getAllRoutineWithDays()
    }

    override suspend fun getHistoryAfterDate(startDate: LocalDate): List<HistoryUiModel> {
        return userDao.getHistoryAfterDate(startDate).map { it.toHistoryUiModel() }
    }
}