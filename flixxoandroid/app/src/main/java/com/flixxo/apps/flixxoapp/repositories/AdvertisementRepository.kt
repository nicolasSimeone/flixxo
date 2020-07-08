package com.flixxo.apps.flixxoapp.repositories

import com.flixxo.apps.flixxoapp.model.AdWatchedResponse
import com.flixxo.apps.flixxoapp.model.AdvertisementResponse
import com.flixxo.apps.flixxoapp.repositories.remote.service.AdvertisementService
import com.flixxo.apps.flixxoapp.utils.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class AdvertisementRepository(private val remoteDataSource: AdvertisementService) {
    suspend fun getAdPlay(): AdvertisementResponse = withContext(Dispatchers.IO) {
        val result = remoteDataSource.getAdPlayer()

        when (result) {
            is Result.Success -> result.data
            is Result.Error -> throw result.exception
        }
    }

    suspend fun adWatched(id: Int): AdWatchedResponse = withContext(Dispatchers.IO) {
        val result = remoteDataSource.adWatched(id)

        when (result) {
            is Result.Success -> result.data
            is Result.Error -> throw result.exception
        }
    }

    suspend fun getAdvertisement(): Double = withContext(Dispatchers.IO) {
        val result = remoteDataSource.getAdvertisement()

        when (result) {
            is Result.Success -> result.data
            is Result.Error -> throw result.exception
        }
    }
}