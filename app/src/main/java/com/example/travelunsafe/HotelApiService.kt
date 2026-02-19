package com.example.travelunsafe

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

// 1. สร้าง Interface กำหนดเส้นทาง URL
interface HotelApiService {
    // ต้องตรงกับ app.get('/api/hotels', ...) ใน server.js
    @GET("api/hotels")
    suspend fun getHotels(): List<Hotel>
}

// 2. ตั้งค่าการเชื่อมต่อ
object RetrofitClient {
    // IP 10.0.2.2 คือคำที่ Emulator ใช้เรียก localhost ของคอมพิวเตอร์เรา
    private const val BASE_URL = "http://10.0.2.2:3001/"

    val apiService: HotelApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()) // ตัวแปลง JSON
            .build()
            .create(HotelApiService::class.java)
    }
}