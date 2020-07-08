package com.flixxo.apps.flixxoapp.repositories.remote.service

import com.flixxo.apps.flixxoapp.model.*
import com.flixxo.apps.flixxoapp.repositories.local.PreferencesManager
import com.flixxo.apps.flixxoapp.utils.Result
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.IOException


class UserService(private val api: ApiClient, private val preferences: PreferencesManager) : BaseService() {

    suspend fun forgotPassword(email: String): Result<Boolean> {
        val response = api.forgotPassword(ForgotPasswordBody(email)).await()
        val body = response.body()
        return if (body == null) {
            Result.Success(false)
        } else {
            val messageError = when (response.code()) {
                200 -> "forgot_code_failed"
                400 -> "find_account_email"
                412 -> "forgot_code_failed"
                else -> "unexpected_error"
            }
            return Result.Error(Exception(messageError))
        }
    }

    suspend fun resetPassword(email: String, code: String, password: String): Result<Boolean> {
        val response = api.resetPassword(ResetPasswordBody(email, code, password)).await()
        val body = response.body()
        body?.let {
            return Result.Success(body.success)
        } ?: run {

            val messageError = when (response.code()) {
                400 -> "password_updating_problem"
                412 -> "password_updating_problem"
                500 -> "password_updating_problem"
                else -> "unexpected_error"
            }

            return Result.Error(Exception(messageError))
        }
    }

    suspend fun changePassword(currentPass: String, newPass: String): Result<Boolean> {
        val response = api.changePassword(ChangePassword(currentPass, newPass)).await()
        val body = response.body()
        body?.let {
            return Result.Success(body.success)
        } ?: run {
            val messageError = when (response.code()) {
                400 -> "invalid_payload"
                403 -> "password_updating_problem"
                else -> "unexpected_error"
            }
            return Result.Error(Exception(messageError))
        }
    }

    suspend fun userProfile(profile: Profile): Result<User> {
        val response = api.userProfile(profile).await()
        val body = response.body()
        body?.let {
            preferences.putString("USER_LANG", it.profile.lang!!)
            preferences.putString("USER_NAME", it.profile.realName!!)
            return Result.Success(body)
        } ?: run {

            val messageError = when (response.code()) {
                500 -> "name_too_long"
                400 -> "error_saving_profile"
                else -> "unexpected_error"
            }

            return Result.Error(Exception(messageError))
        }
    }

    suspend fun getUserStatus(): Result<User> {
        return try {
            val response = api.getUserStatus().await()
            val body = response.body()
            body?.let {
                preferences.putString("NICKNAME_RESUME", it.nickname!!)
                preferences.putString("USER_LANG", it.profile.lang!!)
                preferences.putString("STATUS", body.status.toString())
            }
            handleResult(response)
        } catch (e: Exception) {
            Result.Error(IOException("error_user_status", e))
        }
    }

    suspend fun getFollowers(): Result<List<Author>> {
        return try {
            val response = api.getFollowers().await()
            handleResult(response)
        } catch (e: Exception) {
            Result.Error(IOException("error_get_followers", e))
        }
    }

    suspend fun getClientKeys(): Result<List<ClientKey>> {

        if (preferences.getString("CLIENT_KEY") != null) {
            val clientKey = preferences.getString("CLIENT_KEY")
            return Result.Success(List(1) { ClientKey(clientKey!!, "") })
        } else {

            val response = api.getUserKeys().await()
            val body = response.body()
            body?.let {
                return Result.Success(body)
            } ?: run {

                val messageError = when (response.code()) {
                    400 -> "error_user_keys"
                    else -> "unexpected_error"
                }

                return Result.Error(Exception(messageError))
            }
        }
    }


    fun hasClientKey() = preferences.getString("CLIENT_KEY") != null

    suspend fun getFollowings(): Result<List<Author>> {
        return try {
            val response = api.getFollowings().await()
            handleResult(response)
        } catch (e: Exception) {
            Result.Error(IOException("error_get_followings", e))
        }
    }

    suspend fun getToken(): Result<String> {
        return try {
            val response = api.getToken().await()
            handleResult(response)
        } catch (e: Exception) {
            Result.Error(IOException("error_get_token", e))
        }
    }

    suspend fun createToken(): Result<String> {
        return try {
            val response = api.createToken().await()
            handleResult(response)
        } catch (e: Exception) {
            Result.Error(IOException("error_get_token", e))
        }
    }

    suspend fun getLanguagesFromApi(): Result<List<Language>> {
        return try {
            val response = api.getLanguages().await()
            handleResult(response)
        } catch (e: Exception) {
            Result.Error(IOException("error_get_language", e))
        }
    }


    suspend fun changePhoto(image: File): Result<Profile> {
        val requestBody = RequestBody.create(MediaType.parse("image/*"), image)
        val multipart = MultipartBody.Part.createFormData("image", image.name, requestBody)

        return try {
            val response = api.changePhoto(multipart).await()
            handleResult(response)
        } catch (e: Exception) {
            Result.Error(IOException("error_change_photo", e))
        }
    }


    suspend fun deletePhoto(): Result<Boolean> {
        val response = api.deletePhoto().await()
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

    suspend fun followById(id: String): Result<List<Author>> {
        return try {
            val response = api.followById(id).await()
            handleResult(response)
        } catch (e: Exception) {
            Result.Error(Exception("error_follow_user", e))
        }
    }

    suspend fun unfollowById(id: String): Result<List<Author>> {
        return try {
            val response = api.unfollowById(id).await()
            handleResult(response)
        } catch (e: Exception) {
            Result.Error(Exception("error_unfollow_user", e))
        }
    }
}
