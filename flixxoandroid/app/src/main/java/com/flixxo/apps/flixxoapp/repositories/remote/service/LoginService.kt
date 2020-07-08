package com.flixxo.apps.flixxoapp.repositories.remote.service

import com.flixxo.apps.flixxoapp.model.Category
import com.flixxo.apps.flixxoapp.model.LoginBody
import com.flixxo.apps.flixxoapp.repositories.local.PreferencesManager
import com.flixxo.apps.flixxoapp.utils.Result

class LoginService(private val api: ApiClient, private val preferencesManager: PreferencesManager) {
    suspend fun postLogin(username: String, password: String): Result<Boolean> {
        val response = api.login(LoginBody(username, password)).await()

        val body = response.body()
        body?.let {
            preferencesManager.putString("USER_SECRET", response.body()!!.secret)
            preferencesManager.putString("USER_EMAIL", username)
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

    fun isUserLogged() =
        preferencesManager.getString("STATUS") != "1" && preferencesManager.getString("USER_SECRET") != null

    suspend fun getFollowedCategories(): Result<List<Category>> {
        val response = api.getFollowedCategories().await()
        val body = response.body()
        body?.let {
            return Result.Success(body)
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