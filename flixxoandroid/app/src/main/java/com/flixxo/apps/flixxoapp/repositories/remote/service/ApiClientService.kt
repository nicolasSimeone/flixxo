package com.flixxo.apps.flixxoapp.repositories.remote.service

import com.flixxo.apps.flixxoapp.BuildConfig
import com.flixxo.apps.flixxoapp.model.Author
import com.flixxo.apps.flixxoapp.model.Category
import com.flixxo.apps.flixxoapp.repositories.remote.typeAdapters.AuthorDeserializer
import com.flixxo.apps.flixxoapp.repositories.remote.typeAdapters.CategoryDeserializer
import com.flixxo.apps.flixxoapp.repositories.remote.typeAdapters.StringDeserializer
import com.flixxo.apps.flixxoapp.utils.getBaseUrl
import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class ApiClientService {

    companion object Factory {
        private lateinit var authInterceptor: AuthInterceptor
        private const val TIMEOUT_SERVICE_SECONDS = 15

        fun create(authInterceptor: AuthInterceptor): ApiClient {
            this.authInterceptor = authInterceptor

            val gsonBuilder = GsonBuilder()
            gsonBuilder.registerTypeAdapter(Category::class.java, CategoryDeserializer())
            gsonBuilder.registerTypeAdapter(Author::class.java, AuthorDeserializer())
            gsonBuilder.registerTypeAdapter(String::class.java, StringDeserializer())

            val retrofit = Retrofit.Builder()
                .client(createOkHttpClientBuilder())
                .baseUrl(getBaseUrl())
                .addConverterFactory(GsonConverterFactory.create(gsonBuilder.create()))
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .build()
            return retrofit.create(ApiClient::class.java)
        }

        private fun createOkHttpClientBuilder(): OkHttpClient {
            val builder = OkHttpClient.Builder()
            builder.addInterceptor(authInterceptor)

            if (BuildConfig.DEBUG) {
                val loggingInterceptor = HttpLoggingInterceptor()
                loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
                builder.addInterceptor(loggingInterceptor)
            }

            return builder
                .connectTimeout(TIMEOUT_SERVICE_SECONDS.toLong(), TimeUnit.SECONDS)
                .readTimeout(TIMEOUT_SERVICE_SECONDS.toLong(), TimeUnit.SECONDS)
                .build()
        }
    }

}