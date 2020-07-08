package com.flixxo.apps.flixxoapp.repositories.remote.service

import com.flixxo.apps.flixxoapp.model.Country
import com.flixxo.apps.flixxoapp.utils.Result
import java.io.IOException

class CountryService(private val api: ApiClient) : BaseService() {

    suspend fun getCountries(): Result<List<Country>> {
        return try {
            val response = api.getCountries().await()
            handleResult(response)
        } catch (e: Exception) {
            Result.Error(IOException("error_country", e))
        }
    }
}