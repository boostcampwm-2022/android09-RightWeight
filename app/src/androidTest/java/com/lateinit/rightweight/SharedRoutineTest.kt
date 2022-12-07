package com.lateinit.rightweight

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.gson.annotations.SerializedName
import com.lateinit.rightweight.data.RoutineApiService
import com.lateinit.rightweight.data.database.AppDatabase
import com.lateinit.rightweight.data.database.mediator.*
import com.lateinit.rightweight.data.datasource.RoutineRemoteDataSourceImpl
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
import java.time.LocalDate
import java.time.LocalDateTime
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
                    OrderByData(FieldData("modified_date"), "ASCENDING"),
                    10,
                    StartAtData(ValuesData("2021-11-11T14:56:20.061Z"))
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
    fun updateSharedCountTest() {
        runBlocking {
            //{ fields : { shared_count : { mapValue : { fields : { time: { timestampValue: "2022-12-05T21:03:09.340Z" }, count: { integerValue: 121 } } } } } }
            val rootField = RootField(
                SharedCountField(
                    MapValue(
                        MapValueRootField(
                            SharedCount(
                                TimeStampValue(LocalDateTime.now().toString() + "Z"),
                                IntValue("12345")
                            )
                        )
                    )
                )
            )
            routineApiService.updateSharedRoutineField(
                "8d4bf90a-d1b4-481e-9b75-19fa0bb18842",
                "shared_count",
                rootField
            )
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
                        transform = Transform(
                            path,
                            listOf(FieldTransformsModelData("shared_count.count", IntValue("1")))
                        )
                    )
                )
            )

        }
    }

    data class WriteRequestBody(
        val writes: List<WriteModelData>
    )

    data class WriteModelData(
        @field:SerializedName("transform")
        val transform: Transform,
        val delete: String? = null,
    ) {
        companion object {
            const val defaultPath = "projects/right-weight/databases/(default)/documents"
        }
    }

    data class Transform(
        @field:SerializedName("document")
        val document: String,
        @field:SerializedName("fieldTransforms")
        val fieldTransforms: List<FieldTransformsModelData>
    )

    data class FieldTransformsModelData(
        @field:SerializedName("fieldPath")
        val fieldPath: String,
        @field:SerializedName("increment")
        val increment: IntValue
    )

}