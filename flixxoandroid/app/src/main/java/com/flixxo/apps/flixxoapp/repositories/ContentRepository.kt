package com.flixxo.apps.flixxoapp.repositories

import com.flixxo.apps.flixxoapp.model.Content
import com.flixxo.apps.flixxoapp.model.ContentPurchased
import com.flixxo.apps.flixxoapp.model.NewTorrentFile
import com.flixxo.apps.flixxoapp.model.Series
import com.flixxo.apps.flixxoapp.repositories.local.db.AppDatabase
import com.flixxo.apps.flixxoapp.repositories.remote.service.ContentService
import com.flixxo.apps.flixxoapp.utils.Result
import com.flixxo.apps.flixxoapp.viewModel.HomeViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ContentRepository(private val remoteDataSource: ContentService, private val appDatabase: AppDatabase) {

    suspend fun getTopContent(contentType: String): List<Content> = withContext(Dispatchers.IO) {
        val result = remoteDataSource.getContent(contentType)

        when (result) {
            is Result.Success -> result.data
            is Result.Error -> throw result.exception
        }
    }

    suspend fun getTopSeries(): List<Series> = withContext(Dispatchers.IO) {
        val result = remoteDataSource.getContent(HomeViewModel.HomeContentType.Series.name.toLowerCase())

        when (result) {
            is Result.Success -> {
                val series: MutableList<Series> = mutableListOf()

                for (item in result.data.take(5)) {
                    val detail = remoteDataSource.getSeriesDetail(item.uuid ?: "")

                    if (detail is Result.Success) {
                        series.add(detail.data)
                    }
                }

                series
            }
            is Result.Error -> throw result.exception
        }
    }

    suspend fun getContentDetail(id: String): Content = withContext(Dispatchers.IO) {
        val result = remoteDataSource.getContentDetail(id)

        when (result) {
            is Result.Success -> result.data
            is Result.Error -> throw result.exception
        }
    }

    suspend fun getSearch(word: String?, filter: String?): List<Content> = withContext(Dispatchers.IO) {
        val result = remoteDataSource.getSearch(word, filter)

        when (result) {
            is Result.Success -> result.data
            is Result.Error -> throw result.exception
        }
    }

    suspend fun getContentsByCategoryId(id: Int, contentType: String): List<Content> = withContext(Dispatchers.IO) {
        val result = remoteDataSource.getContentsByCategoryId(id, contentType)

        when (result) {
            is Result.Success -> result.data
            is Result.Error -> throw result.exception
        }
    }

    suspend fun getContentResultsDb(userName: String): List<ContentPurchased> = withContext(Dispatchers.IO) {
        val resultContentDb = appDatabase.purchaseDao().getContentByUser(userName)

        return@withContext resultContentDb
    }

    suspend fun insertContentResultsDb(userName: String, uuid: String) = withContext(Dispatchers.IO) {
        val purchased = ContentPurchased(uuid, userName)
        val resultContentDb = appDatabase.purchaseDao().insertContent(purchased)

        return@withContext resultContentDb
    }

    suspend fun getContentPurchased(userName: String) = withContext(Dispatchers.IO) {

        val result = remoteDataSource.getContentPurchased("all")
        val resultdb = getContentResultsDb(userName)

        when (result) {
            is Result.Success -> {
                for (item in result.data) {
                    if (!resultdb.any({ it.uuid == item.uuid })) {

                        val purchased = ContentPurchased(item.uuid!!, userName)
                        appDatabase.purchaseDao().insertContent(purchased)
                    }
                }
                result.data
            }
            is Result.Error -> throw result.exception
        }

    }

    suspend fun getSeriesDetail(id: String): Series = withContext(Dispatchers.IO) {
        val result = remoteDataSource.getSeriesDetail(id)

        when (result) {
            is Result.Success -> result.data
            is Result.Error -> throw result.exception
        }
    }

    suspend fun getTorrentFile(uuid: String): NewTorrentFile = withContext(Dispatchers.IO) {
        val result = remoteDataSource.getTorrentFile(uuid)

        when (result) {
            is Result.Success -> result.data
            is Result.Error -> throw result.exception
        }
    }
}
