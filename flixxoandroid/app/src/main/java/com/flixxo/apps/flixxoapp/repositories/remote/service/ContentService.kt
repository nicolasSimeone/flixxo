package com.flixxo.apps.flixxoapp.repositories.remote.service

import com.flixxo.apps.flixxoapp.model.Content
import com.flixxo.apps.flixxoapp.model.LoadMessages
import com.flixxo.apps.flixxoapp.model.NewTorrentFile
import com.flixxo.apps.flixxoapp.model.Series
import com.flixxo.apps.flixxoapp.utils.Result

class ContentService(private val api: ApiClient) : BaseService() {

    suspend fun getContent(contentType: String): Result<List<Content>> {
        return try {
            val response = api.getTopContent(contentType).await()
            handleResult(response, response.body()!!.getContent()!!)
        } catch (e: Exception) {
            Result.Error(Exception("error_content", e))
        }
    }

    suspend fun getContentDetail(id: String): Result<Content> {
        return try {
            val response = api.getContentDetail(id).await()
            handleResult(response)
        } catch (e: Exception) {
            Result.Error(Exception("error_content_detail", e))
        }
    }

    suspend fun getSearch(word: String?, filter: String?): Result<List<Content>> {
        val response = api.getSearch(word, filter, "all").await()

        if (!response.isSuccessful) {
            return Result.Error(
                Exception("Error ${response.code()} ${response.message()}")
            )
        }

        val body = response.body()
        body?.let {
            return Result.Success(body.getContent()!!)
        } ?: run {
            return Result.Error(
                Exception("body_null")
            )
        }
    }

    suspend fun getContentsByCategoryId(id: Int, contentType: String): Result<List<Content>> {
        val response = api.getContentsByCategoryId(id, contentType).await()

        if (!response.isSuccessful) {
            return Result.Error(
                Exception("Error ${response.code()} ${response.message()}")
            )
        }

        val body = response.body()
        body?.let {
            return Result.Success(body.getContent()!!)
        } ?: run {
            return Result.Error(
                Exception("body_null")
            )
        }
    }

    suspend fun getContentPurchased(contentType: String): Result<List<Content>> {
        return try {
            val response = api.getContentPurchased(contentType).await()
            handleResult(response, response.body()!!)
        } catch (e: Exception) {
            Result.Error(Exception("error_content", e))
        }
    }

    suspend fun getSeriesDetail(id: String): Result<Series> {
        return try {
            val response = api.getSerieDetail(id).await()
            handleResult(response)
        } catch (e: Exception) {
            Result.Error(Exception("error_content_detail", e))
        }
    }

    suspend fun getTorrentFile(uuid: String): Result<NewTorrentFile> {
        return try {
            val response = api.getTorrentFile(uuid).await()
            handleResult(response)
        } catch (e: Exception) {
            Result.Error(Exception("error_content_detail", e))
        }
    }

    suspend fun getLoadMessages(code: String): Result<LoadMessages> {
        if (code == "es") {

            val response = api.getLoadMessagesEs().await()

            val body = response.body()
            body?.let {
                return Result.Success(body)
            } ?: run {
                return Result.Error(
                    Exception("body_null")
                )
            }
        } else {

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

}
