package com.lateinit.rightweight.data.datasource

import com.lateinit.rightweight.data.RightWeightRetrofitService
import javax.inject.Inject

class LoginDataSourceImpl @Inject constructor(
    val api: RightWeightRetrofitService
) : LoginDataSource {

    fun loginToFirebase(token: String){
        api.loginToFirebase(token, true)
    }
}