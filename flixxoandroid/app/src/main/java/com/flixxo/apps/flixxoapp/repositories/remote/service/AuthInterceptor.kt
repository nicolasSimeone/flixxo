package com.flixxo.apps.flixxoapp.repositories.remote.service

import com.flixxo.apps.flixxoapp.model.Secret
import com.flixxo.apps.flixxoapp.repositories.local.PreferencesManager
import com.flixxo.apps.flixxoapp.utils.CryptoHelper
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class AuthInterceptor(private val preferencesManager: PreferencesManager) : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {

        val request = chain.request()
        val noAuth = request.header("no-auth")
        val builder = request.newBuilder()

        if (noAuth == null) {
            try {
                val secret = preferencesManager.getString("USER_SECRET")
                val generatedSecret = CryptoHelper.genetareToken(Secret(secret!!), chain.request().url().uri().path)
                builder.header("Authorization", String.format("Basic %s", generatedSecret))
            } catch (e: Exception) {
                print(e)
            }

        } else {
            builder.removeHeader("no-auth")
        }

        builder.addHeader("x-flixxo-client", "com.flixxo.app/0.99.9 (linux) Flixxo (development)")
        return chain.proceed(builder.build())
    }
}
