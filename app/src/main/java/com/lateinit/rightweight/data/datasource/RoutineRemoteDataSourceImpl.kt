package com.lateinit.rightweight.data.datasource

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.lateinit.rightweight.data.RoutineApiService
import com.lateinit.rightweight.data.database.AppDatabase
import com.lateinit.rightweight.data.database.mediator.CommunityRemoteMediator
import dagger.hilt.android.HiltAndroidApp
import org.junit.Test
import javax.inject.Inject

class RoutineRemoteDataSourceImpl @Inject constructor(
    val db: AppDatabase,
    val api: RoutineApiService
): RoutineRemoteDataSource {


    @OptIn(ExperimentalPagingApi::class)
    override fun getAllSharedRoutines() = Pager(
        config = PagingConfig(10),
        remoteMediator = CommunityRemoteMediator(
            db, api
        ),
    ) {
        db.sharedRoutineDao().getAllSharedRoutinesByPaging()
    }.flow
}