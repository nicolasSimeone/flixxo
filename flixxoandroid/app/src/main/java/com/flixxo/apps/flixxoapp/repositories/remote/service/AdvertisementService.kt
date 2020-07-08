package com.flixxo.apps.flixxoapp.repositories.remote.service

import com.flixxo.apps.flixxoapp.model.AdWatchedBody
import com.flixxo.apps.flixxoapp.model.AdWatchedResponse
import com.flixxo.apps.flixxoapp.model.AdvertisementResponse
import com.flixxo.apps.flixxoapp.repositories.local.PreferencesManager
import com.flixxo.apps.flixxoapp.utils.Result

class AdvertisementService(private val api: ApiClient, private val preferenceManager: PreferencesManager) {
    suspend fun getAdPlayer(): Result<AdvertisementResponse> {
        val response = api.getAdPlayer().await()
        val body = response.body()
        body?.let {
            return Result.Success(body)
        } ?: run {
            val messageError = when (response.code()) {
                400 -> "user_validation_problem"
                401 -> "unauthorized"
                403 -> "forbidden"
                404 -> "not_found"
                else -> "unexpected_error"
            }
            return Result.Error(Exception(messageError))
        }
    }

    suspend fun adWatched(id: Int): Result<AdWatchedResponse> {
        val response = api.adWatched(AdWatchedBody(id)).await()
        val body = response.body()
        body?.let {
            return Result.Success(body)
        } ?: run {

            val messageError = when (response.code()) {
                400 -> "user_validation_problem"
                401 -> "unauthorized"
                403 -> "forbidden"
                404 -> "not_found"
                else -> "unexpected_error"
            }

            return Result.Error(Exception(messageError))
        }
    }

    suspend fun getAdvertisement(): Result<Double> {
        val response = api.getAdvertisement().await()
        val body = response.body()
        body?.let {
            preferenceManager.putDouble("AD_REWARD", it)
            preferenceManager.putString("FLIXX_REWARD", it.toString())
            return Result.Success(body)
        } ?: run {
            val messageError = when (response.code()) {
                400 -> "user_validation_problem"
                401 -> "unauthorized"
                403 -> "forbidden"
                404 -> "not_found"
                else -> "unexpected_error"
            }
            return Result.Error(Exception(messageError))
        }
    }


}