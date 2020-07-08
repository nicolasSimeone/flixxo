package com.flixxo.apps.flixxoapp.repositories.remote.service

import com.flixxo.apps.flixxoapp.utils.Result
import retrofit2.Response

open class BaseService {
    fun <T : Any> handleResult(response: Response<T>): Result<T> {
        if (!response.isSuccessful) {
            Result.Error(
                Exception("Error ${response.code()} ${response.message()}")
            )
        }

        val body = response.body()
        return body?.let {

            Result.Success(body)
        } ?: run {
            Result.Error(
                Exception("body_null")
            )
        }
    }

    fun <T, U : Any> handleResult(response: Response<T>, returnValue: U): Result<U> {
        if (!response.isSuccessful) {
            Result.Error(
                Exception("Error ${response.code()} ${response.message()}")
            )
        }

        val body = response.body()
        return body?.let {
            Result.Success(returnValue)
        } ?: run {
            Result.Error(
                Exception("body_null")
            )
        }
    }
}