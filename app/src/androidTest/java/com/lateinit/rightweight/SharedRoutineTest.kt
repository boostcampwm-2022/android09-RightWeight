package com.lateinit.rightweight

import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.lateinit.rightweight.data.RoutineApiService
import com.lateinit.rightweight.data.database.mediator.Order
import com.lateinit.rightweight.data.datasource.RoutineRemoteDataSourceImpl
import com.lateinit.rightweight.ui.home.HomeActivity
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.android.testing.CustomTestApplication
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import javax.inject.Inject


@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class SharedRoutineTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var routineRemoteDataSourceImpl: RoutineRemoteDataSourceImpl
    @Inject
    lateinit var routineApiService: RoutineApiService

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    fun a() {
        routineRemoteDataSourceImpl.getAllSharedRoutines()
    }

    @Test
    fun b(){
        runBlocking {
            val appContext = InstrumentationRegistry.getInstrumentation().targetContext
            Assert.assertEquals("com.lateinit.rightweight", appContext.packageName)

            val orderJson = Order("modified_date", 0, 10).toString()
            val sharedRoutines = routineApiService.getSharedRoutines(
                orderJson
            ).fields
            println(sharedRoutines.toString())
        }
    }

}