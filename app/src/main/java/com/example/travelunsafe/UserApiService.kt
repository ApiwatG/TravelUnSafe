package com.example.travelunsafe

import retrofit2.Response
import retrofit2.http.*

interface UserApiService {

    @GET("allUsers")
    suspend fun getAllUsers(): Response<List<User>>

    @GET("deletedUsers")
    suspend fun getDeletedUsers(): Response<List<User>>

    @PATCH("banUser/{id}")
    suspend fun banUser(@Path("id") userId: String): Response<Any>

    @PATCH("unbanUser/{id}")
    suspend fun unbanUser(@Path("id") userId: String): Response<Any>

    @PATCH("softDeleteUser/{id}")
    suspend fun softDeleteUser(@Path("id") userId: String): Response<Any>

    @PATCH("restoreUser/{id}")
    suspend fun restoreUser(@Path("id") userId: String): Response<Any>

    @DELETE("hardDeleteUser/{id}")
    suspend fun hardDeleteUser(@Path("id") userId: String): Response<Any>
}