package com.example.hotel

import retrofit2.Response
import retrofit2.http.*

interface HotelAPI {

    // 1. ดึงข้อมูลโรงแรมทั้งหมด
    @GET("allHotels")
    suspend fun getAllHotels(): Response<List<Hotel>>

    // 2. เพิ่มข้อมูลโรงแรม
    @POST("insertHotel")
    suspend fun insertHotel(@Body hotel: Hotel): Response<Map<String, Any>>

    // 3. แก้ไขข้อมูลโรงแรม
    @PUT("updateHotel/{id}")
    suspend fun updateHotel(
        @Path("id") hotelId: String,
        @Body hotel: Hotel
    ): Response<Map<String, Any>>

    // 4. ลบข้อมูลโรงแรม (Hard Delete)
    @DELETE("deleteHotel/{id}")
    suspend fun deleteHotel(@Path("id") hotelId: String): Response<Map<String, Any>>

    // 5. Soft Delete
    @PATCH("softDeleteHotel/{id}")
    suspend fun softDeleteHotel(@Path("id") hotelId: String): Response<Map<String, Any>>

    @GET("deletedHotels")
    suspend fun getDeletedHotels(): Response<List<Hotel>>

    @PATCH("restoreHotel/{id}")
    suspend fun restoreHotel(@Path("id") hotelId: String): Response<Map<String, Any>>
}
