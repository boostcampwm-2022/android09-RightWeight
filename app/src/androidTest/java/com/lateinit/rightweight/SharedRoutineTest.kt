package com.lateinit.rightweight

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.lateinit.rightweight.data.RoutineApiService
import com.lateinit.rightweight.data.database.AppDatabase
import com.lateinit.rightweight.data.database.mediator.*
import com.lateinit.rightweight.data.datasource.RoutineRemoteDataSourceImpl
import com.lateinit.rightweight.data.model.FieldTransformsModelData
import com.lateinit.rightweight.data.model.TransformData
import com.lateinit.rightweight.data.model.WriteModelData
import com.lateinit.rightweight.data.model.WriteRequestBody
import com.lateinit.rightweight.data.remote.model.*
import com.lateinit.rightweight.util.toSharedRoutine
import com.lateinit.rightweight.util.toSharedRoutineDay
import com.lateinit.rightweight.util.toSharedRoutineExercise
import com.lateinit.rightweight.util.toSharedRoutineExerciseSet
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
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
        db = Room.inMemoryDatabaseBuilder(
            InstrumentationRegistry.getInstrumentation().context,
            AppDatabase::class.java
        ).build()
    }

    @Test
    fun sharedRoutineRequestAndReceive() {
        runBlocking {
            val sharedRoutineRequestBody = SharedRoutineRequestBody(
                StructuredQueryData(
                    FromData("shared_routine"),
                    listOf(OrderByData(FieldData("modified_date"), "ASCENDING")),
                    10,
                    StartAtData(listOf(ValuesData(timestampValue = "2021-11-11T14:56:20.061Z")))
                )
            )
            val documentResponses = routineApiService.getSharedRoutines(
                sharedRoutineRequestBody
            )
            println(documentResponses.toString())

            documentResponses.forEach() { documentResponse ->
                documentResponse.document?.let {
                    db.sharedRoutineDao()
                        .insertSharedRoutine(it.toSharedRoutine())
                }
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
            sharedRoutineDaysResponse?.documents?.forEach() {
                val sharedRoutineDay = it.toSharedRoutineDay()
                println(sharedRoutineDay.toString())
                val sharedRoutineExercisesResponse = routineApiService.getSharedRoutineExercises(
                    sharedRoutineDay.routineId,
                    sharedRoutineDay.dayId
                )
                println(sharedRoutineExercisesResponse.toString())
                sharedRoutineExercisesResponse?.documents?.forEach() {
                    val sharedRoutineExercise = it.toSharedRoutineExercise()
                    println(sharedRoutineExercise.toString())
                    val sharedRoutineExerciseSetsResponse =
                        routineApiService.getSharedRoutineExerciseSets(
                            sharedRoutineDay.routineId,
                            sharedRoutineExercise.dayId,
                            sharedRoutineExercise.exerciseId
                        )
                    sharedRoutineExerciseSetsResponse?.documents?.forEach() {
                        val sharedRoutineExerciseSet = it.toSharedRoutineExerciseSet()
                        println(sharedRoutineExerciseSet.toString())
                    }
                }
            }

        }
    }

    @Test
    fun increaseSharedCountTest() {
        runBlocking {
            val path =
                "${WriteModelData.defaultPath}/shared_routine/50207f59-1eff-4f25-adff-12b4819846c0"
            val writeRequestBody = WriteRequestBody(
                listOf(
                    WriteModelData(
                        transform = TransformData(
                            path,
                            listOf(FieldTransformsModelData("shared_count.count", IntValue("1")))
                        )
                    )
                )
            )
            routineApiService.commitTransaction(writeRequestBody)

        }
    }

    @Test
    fun receiveSharedRoutinesOrderBySharedCount() {
        runBlocking {
            val sharedRoutineRequestBody = SharedRoutineRequestBody(
                StructuredQueryData(
                    FromData("shared_routine"),
                    listOf(OrderByData(FieldData("shared_count.count"), "DESCENDING"),
                        OrderByData(FieldData("modified_date"), "DESCENDING")),
                    10,
                    StartAtData(listOf(ValuesData(integerValue = "9999999"), ValuesData(timestampValue = "9999-11-11T14:56:20.061Z")))
                )
            )
            val documentResponses = routineApiService.getSharedRoutines(
                sharedRoutineRequestBody
            )
            println(documentResponses.toString())

            documentResponses.forEach() { documentResponse ->
                documentResponse.document?.let {
                    db.sharedRoutineDao()
                        .insertSharedRoutine(it.toSharedRoutine())
                }
            }

            println(db.sharedRoutineDao().getAllSharedRoutines())

            //assertEquals("DB_COMPLETE", documentResponses, db.sharedRoutineDao().getAllSharedRoutines())
        }
    }


}