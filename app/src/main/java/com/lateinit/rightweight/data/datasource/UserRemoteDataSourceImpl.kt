package com.lateinit.rightweight.data.datasource

import com.lateinit.rightweight.data.UserApiService
import com.lateinit.rightweight.data.remote.model.RootField
import com.lateinit.rightweight.data.remote.model.StringValue
import com.lateinit.rightweight.data.remote.model.UserInfoField
import javax.inject.Inject

class UserRemoteDataSourceImpl @Inject constructor(
    private val api: UserApiService
) : UserRemoteDataSource {
    override suspend fun backupUserInfo(userId: String, routineId: String?, dayId: String?) {
        api.backupUserInfo(userId, RootField(UserInfoField(
            routineId = StringValue(routineId),
            dayId = StringValue(dayId))
        ))
    }
}