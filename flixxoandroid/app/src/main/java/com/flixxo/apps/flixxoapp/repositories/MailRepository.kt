package com.flixxo.apps.flixxoapp.repositories

import com.flixxo.apps.flixxoapp.model.MailerResponse
import com.flixxo.apps.flixxoapp.repositories.remote.service.MailService
import com.flixxo.apps.flixxoapp.utils.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MailRepository(private val remoteDataSource: MailService) {

    suspend fun sendEmail(): MailerResponse = withContext(Dispatchers.IO) {
        val result = remoteDataSource.sendEmail()

        when (result) {
            is Result.Success -> result.data
            is Result.Error -> throw result.exception
        }
    }
}