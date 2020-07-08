package com.flixxo.apps.flixxoapp.repositories

import com.flixxo.apps.flixxoapp.repositories.remote.service.PaymentsContentService
import com.flixxo.apps.flixxoapp.utils.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PaymentsRepository(private val remoteDataSource: PaymentsContentService) {

    suspend fun payContent(uuid: String, price: Double): Boolean = withContext(Dispatchers.IO) {

        val result = remoteDataSource.payContent(uuid, price)

        when (result) {
            is Result.Success -> result.data
            is Result.Error -> throw result.exception
        }
    }
}
