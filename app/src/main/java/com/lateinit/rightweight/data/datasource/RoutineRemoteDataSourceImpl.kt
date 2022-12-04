package com.lateinit.rightweight.data.datasource

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.lateinit.rightweight.data.RoutineApiService
import com.lateinit.rightweight.data.database.AppDatabase
import com.lateinit.rightweight.data.database.AppSharedPreferences
import com.lateinit.rightweight.data.database.mediator.SharedRoutineRemoteMediator
import javax.inject.Inject

class RoutineRemoteDataSourceImpl @Inject constructor(
    private val db: AppDatabase,
    private val api: RoutineApiService,
    private val appSharedPreferences: AppSharedPreferences
) : RoutineRemoteDataSource {


    @OptIn(ExperimentalPagingApi::class)
    override fun getSharedRoutinesByPaging() = Pager(
        config = PagingConfig(10),
        remoteMediator = SharedRoutineRemoteMediator(
            db, api, appSharedPreferences
        ),
    ) {
        db.sharedRoutineDao().getAllSharedRoutinesByPaging()
    }.flow

    override suspend fun getChildrenDocumentName(path: String): List<String> {
        val documentNameList = api.getChildrenDocumentName(path)
        val documentIdList = documentNameList.documents.map {
            it.name.split("/").last()
        }
        return documentIdList
    }

    override suspend fun deleteDocument(path: String) {
        api.deleteDocument(path)
    }
}