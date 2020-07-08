package com.flixxo.apps.flixxoapp.repositories

import com.flixxo.apps.flixxoapp.model.LoadMessages
import com.flixxo.apps.flixxoapp.repositories.remote.service.TorrentService
import com.flixxo.apps.flixxoapp.utils.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TorrentRepository(private val remoteDataSource: TorrentService) {

    suspend fun getLoadingMessages(code: String): LoadMessages = withContext(Dispatchers.IO) {

        when (val result = if (code == "es") {
            remoteDataSource.getLoadMessagesEs()

        } else {
            remoteDataSource.getLoadMessagesEn()
        }) {
            is Result.Success -> result.data
            is Result.Error -> throw result.exception
        }
    }
}