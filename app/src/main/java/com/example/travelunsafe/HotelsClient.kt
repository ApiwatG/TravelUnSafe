package com.example.travelunsafe

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object HotelsClient {
    // 10.0.2.2 = localhost สำหรับ Android Emulator
    private const val BASE_URL = "http://10.0.2.2:3000/"

    val api: HotelsApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(HotelsApi::class.java)
    }
}