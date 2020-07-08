package com.flixxo.apps.flixxoapp.repositories.remote.service

import com.flixxo.apps.flixxoapp.model.BalanceResponse
import com.flixxo.apps.flixxoapp.model.ClientKey
import com.flixxo.apps.flixxoapp.repositories.local.PreferencesManager
import com.flixxo.apps.flixxoapp.utils.Result
import okhttp3.MediaType
import okhttp3.RequestBody
import java.io.IOException


class MainService(private val api: ApiClient, private val preferencesManager: PreferencesManager) : BaseService() {

    suspend fun getBalance(): Result<BalanceResponse> {

        return try {
            val response = api.getBalance().await()
            handleResult(response)
        } catch (e: Exception) {
            Result.Error(IOException("error_balance", e))
        }
    }

    suspend fun updateClientKey(key: String): Result<List<ClientKey>> {
        return try {

            val body = RequestBody.create(MediaType.parse("text/plain"), key)
            val response = api.sendUserKey(body).await()
            handleResult(response)
        } catch (e: Exception) {
            Result.Error(IOException("error_client_key", e))
        }
    }

    fun getClientSeed() = preferencesManager.getString("CLIENT_SEED")

    fun setClientSeed(seed: String) = preferencesManager.putString("CLIENT_SEED", seed)
}