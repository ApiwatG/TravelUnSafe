package com.example.travelunsafe

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface HotelsApi {

    @POST("register")
    suspend fun register(@Body data: RegisterClass): Response<RegisterResponse>

    @POST("login")
    suspend fun login(@Body data: Map<String, String>): Response<LoginClass>

    @GET("search/{user_id}")
    suspend fun getProfile(@Path("user_id") userId: String): Response<ProfileClass>
}