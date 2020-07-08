package com.flixxo.apps.flixxoapp.repositories.remote.service

import com.flixxo.apps.flixxoapp.model.PaymentsContentBody
import com.flixxo.apps.flixxoapp.utils.Result

class PaymentsContentService(private val api: ApiClient) {

    suspend fun payContent(uuid: String, price: Double): Result<Boolean> {
        val response = api.payContent(PaymentsContentBody(uuid, price)).await()
        val body = response.body()
        body?.let {
            return Result.Success(body.success)
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
