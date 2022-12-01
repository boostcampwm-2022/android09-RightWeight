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
): RemoteMediator<Int, SharedRoutine>() {

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

            val orderJson = Order("modified_date", 0, state.config.pageSize).toString()

            Log.d("orderJsonString", orderJson)

            val sharedRoutines = api.getSharedRoutines(
                orderJson
            ).fields

            db.withTransaction {
                if (loadType == LoadType.REFRESH) {

                }

                sharedRoutines.forEach(){ routineCollection ->
                    db.sharedRoutineDao().insertSharedRoutine(routineCollection.toSharedRoutine())
                }
            }

            return MediatorResult.Success(endOfPaginationReached = sharedRoutines.isEmpty())
        }
        catch (e: IOException) {
            return MediatorResult.Error(e)
        }
        catch (e: HttpException) {
            return MediatorResult.Error(e)
        }
    }


}

data class Order(
    val orderBy : String,
    val startAfter: Int,
    val limit: Int
)