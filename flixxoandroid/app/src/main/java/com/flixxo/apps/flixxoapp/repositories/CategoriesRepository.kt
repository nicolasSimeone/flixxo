package com.flixxo.apps.flixxoapp.repositories

import com.flixxo.apps.flixxoapp.model.Category
import com.flixxo.apps.flixxoapp.repositories.remote.service.CategoriesService
import com.flixxo.apps.flixxoapp.utils.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CategoriesRepository(private val remoteDataSource: CategoriesService) {

    suspend fun getCategories(): List<Category> = withContext(Dispatchers.IO) {
        val result = remoteDataSource.getCategory()

        when (result) {
            is Result.Success -> result.data
            is Result.Error -> throw result.exception
        }
    }

    suspend fun followedCategories(ids: List<Int>): List<Category> = withContext(Dispatchers.IO) {
        val result = remoteDataSource.followedCategories(ids)

        when (result) {
            is Result.Success -> result.data
            is Result.Error -> throw result.exception
        }
    }

}