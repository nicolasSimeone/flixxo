package com.flixxo.apps.flixxoapp.repositories

import com.flixxo.apps.flixxoapp.repositories.remote.service.LoginFacebookService
import com.flixxo.apps.flixxoapp.utils.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LoginFacebookRepository(private val remoteDataSource: LoginFacebookService) {

    suspend fun postLoginFacebook(code: String): Boolean = withContext(Dispatchers.IO) {
        val result = remoteDataSource.postLoginFacebook(code)

        when (result) {
            is Result.Success -> result.data
            is Result.Error -> throw result.exception
        }
    }

}