package com.lateinit.rightweight.data.database.mediator

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.lateinit.rightweight.data.RoutineApiService
import com.lateinit.rightweight.data.database.AppDatabase
import com.lateinit.rightweight.data.database.AppPreferencesDataStore
import com.lateinit.rightweight.data.database.entity.SharedRoutine
import com.lateinit.rightweight.util.toSharedRoutine
import kotlinx.coroutines.flow.first
import retrofit2.HttpException
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class SharedRoutineRemoteMediator(
    private val db: AppDatabase,
    private val api: RoutineApiService,
    private val appPreferencesDataStore: AppPreferencesDataStore
) : RemoteMediator<Int, SharedRoutine>() {

    private val initModifiedDateFlag = "9999-1-1T1:1:1.1Z"

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, SharedRoutine>
    ): MediatorResult {
        try {
            var endOfPaginationReached = false

            var pagingFlag = when (loadType) {
                LoadType.REFRESH -> initModifiedDateFlag
                LoadType.PREPEND -> {
                    endOfPaginationReached = true
                    return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
                }
                LoadType.APPEND -> {
                    val flag = appPreferencesDataStore.sharedRoutinePagingFlag.first()
                    flag.ifEmpty { initModifiedDateFlag }
                }
            }

            val sharedRoutineRequestBody = SharedRoutineRequestBody(
                StructuredQueryData(
                    FromData("shared_routine"),
                    listOf(OrderByData(FieldData("modified_date"), "DESCENDING")),
                    10,
                    StartAtData(listOf(ValuesData(timestampValue = pagingFlag)))
                )
            )

            val documentResponses = api.getSharedRoutines(
                sharedRoutineRequestBody
            )

            db.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    appPreferencesDataStore.saveSharedRoutinePagingFlag("")
                    db.sharedRoutineDao().removeAllSharedRoutines()
                }

                documentResponses.forEach { documentResponse ->
                    if (documentResponse.document != null) {
                        db.sharedRoutineDao()
                            .insertSharedRoutine(documentResponse.document.toSharedRoutine())
                        pagingFlag = documentResponse.document.fields.modifiedDate?.value.toString()
                    } else {
                        endOfPaginationReached = true
                    }
                }
                appPreferencesDataStore.saveSharedRoutinePagingFlag(pagingFlag)
            }

            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
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
    val orderBy: List<OrderByData>,
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
    val values: List<ValuesData>
)

data class ValuesData(
    val integerValue: String? = null,
    val timestampValue: String? = null
)