package com.flixxo.apps.flixxoapp.repositories

import com.flixxo.apps.flixxoapp.model.BalanceResponse
import com.flixxo.apps.flixxoapp.model.ClientKey
import com.flixxo.apps.flixxoapp.repositories.remote.service.MainService
import com.flixxo.apps.flixxoapp.utils.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MainRepository(private val remoteDataSource: MainService) {

    suspend fun getBalance(): BalanceResponse = withContext(Dispatchers.IO) {
        val result = remoteDataSource.getBalance()

        when (result) {
            is Result.Success -> result.data
            is Result.Error -> BalanceResponse(0.0)
        }
    }


    suspend fun updateClientKey(clientKey: String): List<ClientKey> = withContext(Dispatchers.IO) {
        val result = remoteDataSource.updateClientKey(clientKey)

        when (result) {
            is Result.Success -> result.data
            is Result.Error -> throw result.exception
        }
    }

    fun getClientSeed() = remoteDataSource.getClientSeed()

    fun setClientSeed(seed: String) = remoteDataSource.setClientSeed(seed)
}