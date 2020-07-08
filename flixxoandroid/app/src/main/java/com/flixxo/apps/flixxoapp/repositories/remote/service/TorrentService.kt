package com.flixxo.apps.flixxoapp.repositories.remote.service

import com.flixxo.apps.flixxoapp.model.LoadMessages
import com.flixxo.apps.flixxoapp.utils.Result

class TorrentService(private val api: ApiClient) : BaseService() {

    suspend fun getLoadMessagesEs(): Result<LoadMessages> {

        val response = api.getLoadMessagesEs().await()

        val body = response.body()
        body?.let {
            return Result.Success(body)
        } ?: run {
            return Result.Error(
                Exception("body_null")
            )
        }
    }

    suspend fun getLoadMessagesEn(): Result<LoadMessages> {

        val response = api.getLoadMessagesEn().await()

        val body = response.body()
        body?.let {
            return Result.Success(body)
        } ?: run {
            return Result.Error(
                Exception("body_null")
            )
        }
    }
}