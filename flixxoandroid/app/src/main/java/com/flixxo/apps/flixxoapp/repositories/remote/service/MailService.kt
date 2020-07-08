package com.flixxo.apps.flixxoapp.repositories.remote.service

import com.flixxo.apps.flixxoapp.model.MailerBody
import com.flixxo.apps.flixxoapp.model.MailerResponse
import com.flixxo.apps.flixxoapp.repositories.local.PreferencesManager
import com.flixxo.apps.flixxoapp.utils.Result
import java.io.IOException

class MailService(private val api: ApiClient, private val preferences: PreferencesManager) : BaseService() {

    suspend fun sendEmail(): Result<MailerResponse> {

        return try {
            val userEmail = preferences.getString("USER_EMAIL")
            val email = "leandrom@lagash.com"
            val subject = "Buy tokens"
            val message = "User $userEmail wants to buy tokens! Please contact him for coordination."
            val emailBody = MailerBody(email, subject, message)

            val response = api.sendEmail(emailBody).await()
            handleResult(response)
        } catch (e: Exception) {
            Result.Error(IOException("Error sending email", e))
        }
    }
}