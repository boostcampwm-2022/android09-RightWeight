package com.lateinit.rightweight.data.database.mediator

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.lateinit.rightweight.data.RoutineApiService
import com.lateinit.rightweight.data.database.AppDatabase
import com.lateinit.rightweight.data.database.entity.SharedRoutine
import com.lateinit.rightweight.util.toSharedRoutine
import retrofit2.HttpException
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class CommunityRemoteMediator(
    val db: AppDatabase,
    val api: RoutineApiService
) : RemoteMediator<Int, SharedRoutine>() {

    override suspend fun initialize(): RemoteMediator.InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, SharedRoutine>
    ): MediatorResult {
        try {
//            val loadLastIndex = when (loadType) {
//                LoadType.REFRESH -> 1
//                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
//                LoadType.APPEND -> {
//                    val index = db.withTransaction {
//                        shopKeyDao.getRemoteKey()
//                    }.key
//                    index
//                }
//            }

//            val orderJson = Order("modified_date", 0, state.config.pageSize).toString()
//
//            val documentResponses = api.getSharedRoutines(
//                orderJson
//            )

            val newOrder = NewOrder(
                StructuredQueryData(
                    FromData(""),
                    OrderByData(FieldData("modified_date"), "ASCENDING"),
                    10,
                    StartAtData(ValuesData("2021-11-11T14:56:20.061Z"))
                )
            )

            val documentResponses = api.getSharedRoutines(
                newOrder
            )

            db.withTransaction {
                if (loadType == LoadType.REFRESH) {

                }

                documentResponses.forEach() { documentResponse ->
                    db.sharedRoutineDao()
                        .insertSharedRoutine(documentResponse.document.fields.toSharedRoutine())
                }
            }

            return MediatorResult.Success(endOfPaginationReached = documentResponses.isEmpty())
        } catch (e: IOException) {
            return MediatorResult.Error(e)
        } catch (e: HttpException) {
            return MediatorResult.Error(e)
        }
    }


}

//data class Order(
//    val orderBy: String,
//    val startAfter: Int,
//    val limit: Int
//) {
//    override fun toString(): String {
//        return """{ "structuredQuery": {  "from": [ { "collectionId": "shared_routine" }], "orderBy": [{"field": {"fieldPath": "modified_date"}, "direction": "ASCENDING"  }], "limit": 10,  "startAt": {"values": [{"timestampValue": "2021-11-11T14:56:20.061Z"}]}}}"""
//        //return """ "structuredQuery": { "from": [ { "collectionId": "shared_routine" }] } """
//        //return  """{ "structuredQuery": { "limit": $limit, "orderBy": [{"field": {"fieldPath": $orderBy} }], "startAfter": "values": [{"stringValue": $startAfter}] }}"""
//    }
//}

data class NewOrder(
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