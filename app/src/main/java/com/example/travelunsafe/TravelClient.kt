package com.example.travelunsafe

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object TravelClient {

    // ⚠️ Change to your LAN IP when testing on real device (e.g. 192.168.1.x:3000)
    // Use 10.0.2.2:3000 for Android Emulator
    private const val BASE_URL = "http://192.168.1.11:3000/"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)    // longer for image upload
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    val travelAPI: TravelAPI by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TravelAPI::class.java)
    }
}