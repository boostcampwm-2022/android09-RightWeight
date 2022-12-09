package com.lateinit.rightweight.data.mediator

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.lateinit.rightweight.data.api.RoutineApiService
import com.lateinit.rightweight.data.database.AppDatabase
import com.lateinit.rightweight.data.dataStore.AppPreferencesDataStore
import com.lateinit.rightweight.data.database.entity.SharedRoutine
import com.lateinit.rightweight.data.model.remote.FiledReferenceData
import com.lateinit.rightweight.data.model.remote.FromData
import com.lateinit.rightweight.data.model.remote.OrderByData
import com.lateinit.rightweight.data.model.remote.RunQueryBody
import com.lateinit.rightweight.data.model.remote.SharedRoutineSortType
import com.lateinit.rightweight.data.model.remote.StartAtData
import com.lateinit.rightweight.data.model.remote.StructuredQueryData
import com.lateinit.rightweight.data.remote.model.IntValue
import com.lateinit.rightweight.data.remote.model.TimeStampValue
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

    private lateinit var runQueryBody: RunQueryBody

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
                LoadType.REFRESH -> "$INIT_MODIFIED_DATE_FLAG/$INIT_SHARED_COUNT_FLAG"
                LoadType.PREPEND -> {
                    endOfPaginationReached = true
                    return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
                }
                LoadType.APPEND -> {
                    val flag = appPreferencesDataStore.sharedRoutinePagingFlag.first()
                    flag.ifEmpty { "$INIT_MODIFIED_DATE_FLAG/$INIT_SHARED_COUNT_FLAG" }
                }
            }

            val splitedPagingFlag = pagingFlag.split("/")
            var modifiedDateFlag = splitedPagingFlag[0]
            var sharedCountFlag = splitedPagingFlag[1]

            when (sortType) {
                SharedRoutineSortType.MODIFIED_DATE_FIRST -> {
                    runQueryBody = RunQueryBody(
                        StructuredQueryData(
                            from = FromData("shared_routine"),
                            orderBy = listOf(
                                OrderByData(FiledReferenceData("modified_date"), "DESCENDING"),
                                OrderByData(FiledReferenceData("shared_count.count"), "DESCENDING")
                            ),
                            limit = 10,
                            startAt = StartAtData(
                                listOf(
                                    TimeStampValue(modifiedDateFlag),
                                    IntValue(sharedCountFlag)
                                )
                            )
                        )
                    )
                }
                SharedRoutineSortType.SHARED_COUNT_FIRST -> {
                    runQueryBody = RunQueryBody(
                        StructuredQueryData(
                            FromData("shared_routine"),
                            orderBy = listOf(
                                OrderByData(FiledReferenceData("shared_count.count"), "DESCENDING"),
                                OrderByData(FiledReferenceData("modified_date"), "DESCENDING")
                            ),
                            limit = 10,
                            startAt = StartAtData(
                                listOf(
                                    IntValue(sharedCountFlag),
                                    TimeStampValue(modifiedDateFlag)
                                )
                            )
                        )
                    )
                }
            }

            val documentResponses = api.getSharedRoutines(
                runQueryBody
            )

            db.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    appPreferencesDataStore.saveSharedRoutinePagingFlag("$INIT_MODIFIED_DATE_FLAG/$INIT_SHARED_COUNT_FLAG")
                    db.sharedRoutineDao().removeAllSharedRoutines()
                }

                documentResponses.forEach { documentResponse ->
                    if (documentResponse.document != null) {
                        db.sharedRoutineDao()
                            .insertSharedRoutine(documentResponse.document.toSharedRoutine())
                        modifiedDateFlag =
                            documentResponse.document.fields.modifiedDate.value.toString()
                        sharedCountFlag =
                            documentResponse.document.fields.sharedCount.value?.remoteData?.count?.value.toString()
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

    companion object {
        const val INIT_MODIFIED_DATE_FLAG = "9999-1-1T1:1:1.1Z"
        const val INIT_SHARED_COUNT_FLAG = "999999999"
    }

}


