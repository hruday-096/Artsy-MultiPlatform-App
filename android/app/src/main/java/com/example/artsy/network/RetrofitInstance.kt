package com.example.artsy.network

import android.content.Context
import com.franmontiel.persistentcookiejar.ClearableCookieJar
import com.franmontiel.persistentcookiejar.PersistentCookieJar
import com.franmontiel.persistentcookiejar.cache.SetCookieCache
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import android.util.Log


object RetrofitInstance {

    private const val BASE_URL = "https://artsyauth.uc.r.appspot.com/"

    // Will be initialized with context
    private lateinit var cookieJar: ClearableCookieJar

    // Retrofit instance that depends on initialized context
    private lateinit var retrofit: Retrofit

    val api: ArtsyApiService by lazy {
        retrofit.create(ArtsyApiService::class.java)
    }

    fun init(context: Context) {
        Log.d("RetrofitDebug", "RetrofitInstance.init() called")
        cookieJar = PersistentCookieJar(
            SetCookieCache(),
            SharedPrefsCookiePersistor(context)
        )

        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .cookieJar(cookieJar)
            .addInterceptor(loggingInterceptor)
            .build()

        val json = Json {
            ignoreUnknownKeys = true
        }

        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }

    fun clearCookies() {
        if (::cookieJar.isInitialized) {
            cookieJar.clear()
        }
    }
}
