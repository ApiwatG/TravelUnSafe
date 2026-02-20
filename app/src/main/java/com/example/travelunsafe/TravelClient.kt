package com.example.travelunsafe

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object TravelClient {

    // ⚠️ Change this to your server IP when testing on a real device
    // Use 10.0.2.2 for Android Emulator (maps to your localhost)
    // Use your actual LAN IP (e.g. 192.168.1.x) for real device
    private const val BASE_URL = "http://192.168.1.3:3000/"

    val travelAPI: TravelAPI by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TravelAPI::class.java)
    }
}
