package com.lateinit.rightweight

import androidx.room.Room
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.gson.JsonObject
import com.lateinit.rightweight.data.RoutineApiService
import com.lateinit.rightweight.data.database.AppDatabase
import com.lateinit.rightweight.data.database.mediator.*
import com.lateinit.rightweight.data.datasource.RoutineRemoteDataSourceImpl
import com.lateinit.rightweight.ui.home.HomeActivity
import com.lateinit.rightweight.util.toSharedRoutine
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.android.testing.CustomTestApplication
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import junit.framework.Assert.assertEquals
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

//    @Inject
//    lateinit var db: AppDatabase
    lateinit var db: AppDatabase

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Before
    fun initDb() {
        db = Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getInstrumentation().context,
            AppDatabase::class.java).build()
    }

    @Test
    fun a() {
        routineRemoteDataSourceImpl.getAllSharedRoutines()
    }

    @Test
    fun b() {
        runBlocking {

//            val orderJson = Order("modified_date", 0, 10).toString()
//            val documentResponses = routineApiService.getSharedRoutines(
//                orderJson
//            )
            val newOrder = NewOrder(
                StructuredQueryData(
                    FromData("shared_routine"),
                    OrderByData(FieldData("modified_date"), "ASCENDING"),
                    10,
                    StartAtData(ValuesData("2021-11-11T14:56:20.061Z"))
                )
            )
            val documentResponses = routineApiService.getSharedRoutines(
                newOrder
            )
            println(documentResponses.toString())

            documentResponses.forEach() { documentResponse ->
                db.sharedRoutineDao()
                    .insertSharedRoutine(documentResponse.document.fields.toSharedRoutine())
            }

            println(db.sharedRoutineDao().getAllSharedRoutines())

            assertEquals("DB_COMPLETE", documentResponses, db.sharedRoutineDao().getAllSharedRoutines())
        }
    }

}