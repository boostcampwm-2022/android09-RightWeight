package com.lateinit.rightweight.data.database.mediator

import androidx.annotation.StringRes
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.lateinit.rightweight.R
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
    private val appPreferencesDataStore: AppPreferencesDataStore,
    var sortType: SharedRoutineSortType
) : RemoteMediator<Int, SharedRoutine>() {

    private val initModifiedDateFlag = "9999-1-1T1:1:1.1Z"
    private val initSharedCountFlag = "999999999"

    lateinit var sharedRoutineRequestBody: SharedRoutineRequestBody

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
                LoadType.REFRESH -> "$initModifiedDateFlag/$initSharedCountFlag"
                LoadType.PREPEND -> {
                    endOfPaginationReached = true
                    return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
                }
                LoadType.APPEND -> {
                    val flag = appPreferencesDataStore.sharedRoutinePagingFlag.first()
                    flag.ifEmpty { "$initModifiedDateFlag/$initSharedCountFlag" }
                }
            }

            val splitedPagingFlag = pagingFlag.split("/")
            var modifiedDateFlag = splitedPagingFlag[0]
            var sharedCountFlag = splitedPagingFlag[1]

            when(sortType){
                SharedRoutineSortType.MODIFIED_DATE_FIRST ->{
                    sharedRoutineRequestBody = SharedRoutineRequestBody(
                        StructuredQueryData(
                            FromData("shared_routine"),
                            listOf(OrderByData(FieldData("modified_date"), "DESCENDING"),
                                OrderByData(FieldData("shared_count.count"), "DESCENDING")),
                            10,
                            StartAtData(listOf(ValuesData(timestampValue = modifiedDateFlag), ValuesData(integerValue = sharedCountFlag)))
                        )
                    )
                }
                SharedRoutineSortType.SHARED_COUNT_FIRST ->{
                    sharedRoutineRequestBody = SharedRoutineRequestBody(
                        StructuredQueryData(
                            FromData("shared_routine"),
                            listOf(OrderByData(FieldData("shared_count.count"), "DESCENDING"),
                                OrderByData(FieldData("modified_date"), "DESCENDING")),
                            10,
                            StartAtData(listOf(ValuesData(integerValue = sharedCountFlag), ValuesData(timestampValue = modifiedDateFlag)))
                        )
                    )
                }
            }

            val documentResponses = api.getSharedRoutines(
                sharedRoutineRequestBody
            )

            db.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    appPreferencesDataStore.saveSharedRoutinePagingFlag("$initModifiedDateFlag/$initSharedCountFlag")
                    db.sharedRoutineDao().removeAllSharedRoutines()
                }

                documentResponses.forEach { documentResponse ->
                    if (documentResponse.document != null) {
                        db.sharedRoutineDao()
                            .insertSharedRoutine(documentResponse.document.toSharedRoutine())
                        modifiedDateFlag = documentResponse.document.fields.modifiedDate?.value.toString()
                        sharedCountFlag = documentResponse.document.fields.sharedCount?.value?.remoteData?.count?.value.toString()
                        pagingFlag = "$modifiedDateFlag/$sharedCountFlag"
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

enum class SharedRoutineSortType(@StringRes val sortTypeName: Int){
    MODIFIED_DATE_FIRST(R.string.modified_date_sort), SHARED_COUNT_FIRST(R.string.shared_count_sort)
}