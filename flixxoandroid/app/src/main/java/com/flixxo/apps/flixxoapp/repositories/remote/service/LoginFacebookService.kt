package com.flixxo.apps.flixxoapp.repositories.remote.service

import com.flixxo.apps.flixxoapp.repositories.local.PreferencesManager
import com.flixxo.apps.flixxoapp.utils.Result

class LoginFacebookService(private val api: ApiClient, private val preferencesManager: PreferencesManager) {
    suspend fun postLoginFacebook(code: String): Result<Boolean> {
        val response = api.loginFacebook(LoginFacebookBody(code)).await()

        val body = response.body()
        body?.let {
            preferencesManager.putString("USER_SECRET", response.body()!!.secret)
            return Result.Success(true)
        } ?: run {

            val messageError = when (response.code()) {
                400 -> "login_unsuccess"
                401 -> "bad_credentials"
                403 -> "not_able_login"
                409 -> "blocked_login"
                else -> "unexpected_error"
            }

            return Result.Error(Exception(messageError))

        }
    }

}