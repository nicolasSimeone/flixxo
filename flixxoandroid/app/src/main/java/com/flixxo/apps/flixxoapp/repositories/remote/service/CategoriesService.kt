package com.flixxo.apps.flixxoapp.repositories.remote.service

import com.flixxo.apps.flixxoapp.model.Category
import com.flixxo.apps.flixxoapp.utils.Result

class CategoriesService(private val api: ApiClient) : BaseService() {

    suspend fun getCategory(): Result<List<Category>> {
        val response = api.getCategories().await()

        if (!response.isSuccessful) {
            return Result.Error(
                Exception("Error ${response.code()} ${response.message()}")
            )
        }

        val body = response.body()
        body?.let {
            return Result.Success(body)
        } ?: run {
            return Result.Error(
                Exception("body_null")
            )
        }
    }

    suspend fun followedCategories(ids: List<Int>): Result<List<Category>> {
        return try {
            val response = api.followedCategories(ids).await()
            handleResult(response)
        } catch (e: Exception) {
            Result.Error(Exception("Error getting content", e))
        }
    }

}