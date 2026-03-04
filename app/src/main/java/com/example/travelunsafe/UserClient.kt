package com.example.travelunsafe



import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object UserClient {
    val instance: UserApiService by lazy {
        Retrofit.Builder()
            .baseUrl("http://10.0.2.2:3000/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(UserApiService::class.java)
    }
}