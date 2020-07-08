package com.flixxo.apps.flixxoapp.repositories.remote.service

import com.flixxo.apps.flixxoapp.model.Confirm
import com.flixxo.apps.flixxoapp.model.EmailBody
import com.flixxo.apps.flixxoapp.repositories.local.PreferencesManager
import com.flixxo.apps.flixxoapp.utils.Result
import java.text.SimpleDateFormat
import java.util.*


class ConfirmCodeService(private val api: ApiClient, private val preferencesManager: PreferencesManager) :
    BaseService() {

    suspend fun confirmSMS(code: String): Result<Boolean> {
        val email = preferencesManager.getString("USER_EMAIL")
        val response = api.confirmSMS(Confirm(email, code)).await()
        val body = response.body()
        return if (body == null) {
            Result.Success(false)
        } else {
            val messageError = when (response.code()) {
                200 -> "invalid_code"
                400 -> "confirmation_failed"
                403 -> "forbidden"
                else -> "unexpected_error"
            }
            Result.Error(Exception(messageError))
        }
    }

    suspend fun resendCodeSMS(): Result<Boolean> {
        val email = preferencesManager.getString("USER_EMAIL")
        val response = api.resendCodeSMS(EmailBody(email)).await()
        val body = response.body()
        return if (body == null) {
            Result.Success(true)
        } else {
            val calendar = Calendar.getInstance(Locale.getDefault())
            val freeAt = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(body.freeAt)
            val currentHour = calendar[Calendar.MINUTE]
            val hourMinutes = SimpleDateFormat("HH:mm").format(freeAt.time - 10800 * 1000)
            val nextTime = (hourMinutes.takeLast(2).toInt() - currentHour)
            val messageError = when (response.code()) {
                200 -> nextTime.toString()
                403 -> "disabled_code"
                else -> "unexpected_error"
            }
            Result.Error(Exception(messageError))
        }
    }
}
