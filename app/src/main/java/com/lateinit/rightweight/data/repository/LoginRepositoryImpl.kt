package com.lateinit.rightweight.data.repository

import com.lateinit.rightweight.data.RightWeightRetrofitService
import com.lateinit.rightweight.data.datasource.LoginDataSource
import javax.inject.Inject

class LoginRepositoryImpl @Inject constructor(
    val loginDataSource: LoginDataSource
) : LoginRepository {

}