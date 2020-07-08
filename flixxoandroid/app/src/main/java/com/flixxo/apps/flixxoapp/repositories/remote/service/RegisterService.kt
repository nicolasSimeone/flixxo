package com.flixxo.apps.flixxoapp.repositories.remote.service

import com.flixxo.apps.flixxoapp.repositories.local.PreferencesManager
import com.flixxo.apps.flixxoapp.utils.Result
import org.json.JSONObject


class RegisterService(private val api: ApiClient, private val preferencesManager: PreferencesManager) {
    suspend fun postRegister(
        nickname: String,
        emailRegister: String,
        passwordRegister: String,
        code: String,
        mobile: Boolean,
        mobileNumber: String
    ): Result<Boolean> {
        val response =
            api.register(RegisterBody(nickname, emailRegister, passwordRegister, code, mobile, mobileNumber)).await()

        val body = response.body()

        body?.let {
            preferencesManager.putString("USER_SECRET", response.body()!!.secret!!)
            preferencesManager.putString("USER_EMAIL", emailRegister)
            preferencesManager.putString("PHONE_NUMBER", mobileNumber)
            return Result.Success(true)
        } ?: run {

            val jObjError = JSONObject(response.errorBody()!!.string())
            val message = jObjError.getJSONObject("error").getString("message")
            val messageError = when (response.code()) {
                400 -> if (message == "Nickname already in use") "nick_name_user" else "email_registered"
                else -> "unexpected_error"
            }

            return Result.Error(Exception(messageError))

        }

    }

}
