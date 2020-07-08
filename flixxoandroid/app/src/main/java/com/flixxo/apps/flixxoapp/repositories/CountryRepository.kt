package com.flixxo.apps.flixxoapp.repositories

import com.flixxo.apps.flixxoapp.model.Country
import com.flixxo.apps.flixxoapp.repositories.remote.service.CountryService
import com.flixxo.apps.flixxoapp.utils.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CountryRepository(private val remoteDataSource: CountryService) {

    suspend fun getCountries(): List<Country> = withContext(Dispatchers.IO) {
        val result = remoteDataSource.getCountries()

        when (result) {
            is Result.Success -> result.data
            is Result.Error -> throw result.exception
        }
    }
}