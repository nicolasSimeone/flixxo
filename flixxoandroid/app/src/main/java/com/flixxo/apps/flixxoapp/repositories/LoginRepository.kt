package com.flixxo.apps.flixxoapp.repositories

import com.flixxo.apps.flixxoapp.model.Category
import com.flixxo.apps.flixxoapp.repositories.remote.service.LoginService
import com.flixxo.apps.flixxoapp.utils.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LoginRepository(private val remoteDataSource: LoginService) {

    suspend fun postLogin(username: String, password: String): Boolean = withContext(Dispatchers.IO) {
        val result = remoteDataSource.postLogin(username, password)

        when (result) {
            is Result.Success -> result.data
            is Result.Error -> throw result.exception
        }
    }

    suspend fun getFollowedCategories() : List<Category> = withContext(Dispatchers.IO) {
        when(val result = remoteDataSource.getFollowedCategories()) {
            is Result.Success -> result.data
            is Result.Error -> throw result.exception
        }
    }

    fun isUserLogged() = remoteDataSource.isUserLogged()
}