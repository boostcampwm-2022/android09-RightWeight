package com.lateinit.rightweight.data.database.mediator

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.lateinit.rightweight.data.RoutineApiService
import com.lateinit.rightweight.data.database.AppDatabase
import com.lateinit.rightweight.data.database.AppSharedPreferences
import com.lateinit.rightweight.data.database.entity.SharedRoutine
import com.lateinit.rightweight.util.toSharedRoutine
import retrofit2.HttpException
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class SharedRoutineRemoteMediator(
    private val db: AppDatabase,
    private val api: RoutineApiService,
    private val appSharedPreferences: AppSharedPreferences
) : RemoteMediator<Int, SharedRoutine>() {

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, SharedRoutine>
    ): MediatorResult {
        try {
            var pagingFlag= when (loadType) {
                LoadType.REFRESH -> ""
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {
                    appSharedPreferences.getSharedRoutinePagingFlag()
                }
            }

            val sharedRoutineRequestBody = SharedRoutineRequestBody(
                StructuredQueryData(
                    FromData(""),
                    OrderByData(FieldData("modified_date"), "ASCENDING"),
                    10,
                    StartAtData(ValuesData(pagingFlag))
                )
            )

            val documentResponses = api.getSharedRoutines(
                sharedRoutineRequestBody
            )

            db.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    appSharedPreferences.setSharedRoutinePagingFlag("")
                    db.sharedRoutineDao().removeAllSharedRoutines()
                }

                documentResponses.forEach() { documentResponse ->
                    db.sharedRoutineDao()
                        .insertSharedRoutine(documentResponse.document.toSharedRoutine())
                    pagingFlag = documentResponse.document.fields.modifiedDate.toString()
                }
                appSharedPreferences.setSharedRoutinePagingFlag(pagingFlag)
            }

            return MediatorResult.Success(endOfPaginationReached = documentResponses.isEmpty())
        } catch (e: IOException) {
            return MediatorResult.Error(e)
        } catch (e: HttpException) {
            return MediatorResult.Error(e)
        }
    }

}

data class SharedRoutineRequestBody(
    val structuredQuery: StructuredQueryData
)

data class StructuredQueryData(
    val from: FromData,
    val orderBy: OrderByData,
    val limit: Int,
    val startAt: StartAtData
)

data class FromData(
    val collectionId: String,
)

data class OrderByData(
    val field: FieldData,
    val direction: String
)

data class FieldData(
    val fieldPath: String
)

data class StartAtData(
    val values: ValuesData
)

data class ValuesData(
    val timestampValue: String
)