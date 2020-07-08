package com.flixxo.apps.flixxoapp.repositories

import com.flixxo.apps.flixxoapp.repositories.remote.service.RegisterService
import com.flixxo.apps.flixxoapp.utils.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RegisterRepository(private val remoteDataSource: RegisterService) {

    suspend fun postRegister(
        nickname: String,
        emailRegister: String,
        passwordRegister: String,
        code: String,
        mobile: Boolean,
        mobileNumber: String
    ): Boolean = withContext(Dispatchers.IO) {
        val result =
            remoteDataSource.postRegister(nickname, emailRegister, passwordRegister, code, mobile, mobileNumber)

        when (result) {
            is Result.Success -> result.data
            is Result.Error -> throw result.exception
        }

    }
}
