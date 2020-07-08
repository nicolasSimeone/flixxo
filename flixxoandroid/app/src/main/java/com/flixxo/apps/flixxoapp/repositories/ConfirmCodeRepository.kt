package com.flixxo.apps.flixxoapp.repositories

import com.flixxo.apps.flixxoapp.repositories.remote.service.ConfirmCodeService
import com.flixxo.apps.flixxoapp.utils.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ConfirmCodeRepository(private val remoteDataSource: ConfirmCodeService) {

    suspend fun confirmSMS(code: String): Boolean = withContext(Dispatchers.IO) {
        val result = remoteDataSource.confirmSMS(code)

        when (result) {
            is Result.Success -> result.data
            is Result.Error -> throw result.exception
        }
    }

    suspend fun resendCodeSMS(): Boolean = withContext(Dispatchers.IO) {
        val result = remoteDataSource.resendCodeSMS()

        when (result) {
            is Result.Success -> result.data
            is Result.Error -> throw result.exception
        }
    }
}