package com.lateinit.rightweight

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.lateinit.rightweight.data.RoutineApiService
import com.lateinit.rightweight.data.database.AppDatabase
import com.lateinit.rightweight.data.database.mediator.*
import com.lateinit.rightweight.data.datasource.RoutineRemoteDataSourceImpl
import com.lateinit.rightweight.util.toSharedRoutine
import com.lateinit.rightweight.util.toSharedRoutineDay
import com.lateinit.rightweight.util.toSharedRoutineExercise
import com.lateinit.rightweight.util.toSharedRoutineExerciseSet
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
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
    fun sharedRoutineRequestAndReceive() {
        runBlocking {
            val sharedRoutineRequestBody = SharedRoutineRequestBody(
                StructuredQueryData(
                    FromData("shared_routine"),
                    OrderByData(FieldData("modified_date"), "ASCENDING"),
                    10,
                    StartAtData(ValuesData("2021-11-11T14:56:20.061Z"))
                )
            )
            val documentResponses = routineApiService.getSharedRoutines(
                sharedRoutineRequestBody
            )
            println(documentResponses.toString())

            documentResponses?.forEach() { documentResponse ->
                db.sharedRoutineDao()
                    .insertSharedRoutine(documentResponse.document.toSharedRoutine())
            }

            println(db.sharedRoutineDao().getAllSharedRoutines())

            //assertEquals("DB_COMPLETE", documentResponses, db.sharedRoutineDao().getAllSharedRoutines())
        }
    }

    @Test
    fun sharedRoutineDetailTest() {
        runBlocking {
            val sharedRoutineDaysResponse = routineApiService.getSharedRoutineDays(
                "07fd07ab-45c6-4479-881a-abe151a82456"
            )
            sharedRoutineDaysResponse?.documents?.forEach(){
                val sharedRoutineDay = it.toSharedRoutineDay()
                println(sharedRoutineDay.toString())
                val sharedRoutineExercisesResponse= routineApiService.getSharedRoutineExercises(
                    sharedRoutineDay.routineId,
                    sharedRoutineDay.dayId
                )
                println(sharedRoutineExercisesResponse.toString())
                sharedRoutineExercisesResponse?.documents?.forEach(){
                    val sharedRoutineExercise = it.toSharedRoutineExercise()
                    println(sharedRoutineExercise.toString())
                    val sharedRoutineExerciseSetsResponse = routineApiService.getSharedRoutineExerciseSets(
                        sharedRoutineDay.routineId,
                        sharedRoutineExercise.dayId,
                        sharedRoutineExercise.exerciseId
                    )
                   sharedRoutineExerciseSetsResponse?.documents?.forEach(){
                       val sharedRoutineExerciseSet = it.toSharedRoutineExerciseSet()
                       println(sharedRoutineExerciseSet.toString())
                   }
                }
            }

        }
    }

}