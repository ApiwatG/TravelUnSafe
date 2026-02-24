package com.example.travelunsafe

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface TravelApiService {

    // ===== USERS =====
    @POST("api/login")
    suspend fun login(@Body request: LoginRequest): User

    @POST("api/register")
    suspend fun register(@Body request: RegisterRequest): RegisterResponse

    @GET("api/profile/{id}")
    suspend fun getProfile(@Path("id") userId: String): ProfileSummary

    // ===== HOTELS =====
    @GET("api/hotels")
    suspend fun getHotels(): List<Hotel>

    @GET("api/hotels/{id}")
    suspend fun getHotelById(@Path("id") id: String): Hotel

    // ===== PLACES =====
    @GET("api/places")
    suspend fun getPlaces(): List<Place>

    // ===== TRIPS =====
    @GET("api/trips/user/{userId}")
    suspend fun getUserTrips(@Path("userId") userId: String): List<Trip>

    @POST("api/trips")
    suspend fun createTrip(@Body request: CreateTripRequest): CreateTripResponse

    // ===== BOOKINGS =====
    @GET("api/bookings/user/{userId}")
    suspend fun getUserBookings(@Path("userId") userId: String): List<Booking>

    @POST("api/bookings")
    suspend fun createBooking(@Body request: CreateBookingRequest): CreateBookingResponse

    // ===== FAVORITES =====
    @GET("api/favorites/user/{userId}")
    suspend fun getUserFavorites(@Path("userId") userId: String): List<FavoritePlace>

    @POST("api/favorites")
    suspend fun addFavorite(@Body request: AddFavoriteRequest): AddFavoriteResponse

    // เพิ่ม 3 ฟังก์ชันนี้เข้าไปใน interface TravelApiService
    @GET("api/trips/{id}")
    suspend fun getTripById(@Path("id") id: String): Trip

    @GET("api/itinerarys/trip/{tripId}")
    suspend fun getItineraryByTrip(@Path("tripId") tripId: String): List<Itinerary>

    @GET("api/expenses/trip/{tripId}")
    suspend fun getExpensesByTrip(@Path("tripId") tripId: String): List<Expense>

    @POST("api/expenses")
    suspend fun createExpense(@Body request: CreateExpenseRequest): ApiResponse

    // 💡 เส้นทางสำหรับเพิ่มสถานที่ในทริป
    @POST("api/itinerarys")
    suspend fun createItinerary(@Body request: CreateItineraryRequest): ApiResponse

    // 💡 เส้นทางสำหรับดึงรายชื่อเพื่อน
    @GET("api/friends/{userId}")
    suspend fun getFriends(@Path("userId") userId: String): List<Friend>

    // 💡 เส้นทางสำหรับแก้ไขชื่อทริป
    @PUT("api/trips/{id}")
    suspend fun updateTripName(@Path("id") tripId: String, @Body request: UpdateTripRequest): ApiResponse

    // ดึงสมาชิกในทริป
    @GET("api/trips/{tripId}/members")
    suspend fun getTripMembers(@Path("tripId") tripId: String): List<Friend>

    // เพิ่มเพื่อนเข้าทริป
    @POST("api/trips/{tripId}/members")
    suspend fun addTripMember(@Path("tripId") tripId: String, @Body request: AddMemberRequest): ApiResponse

    // ลบเพื่อนออกจากทริป
    @DELETE("api/trips/{tripId}/members/{userId}")
    suspend fun removeTripMember(@Path("tripId") tripId: String, @Path("userId") userId: String): ApiResponse
}





// ==========================================
// การสร้าง Retrofit Client สำหรับยิง API
// ==========================================
object TravelApiClient {
    // 💡 หากใช้ Emulator เปลี่ยน localhost เป็น 10.0.2.2
    // หากทดสอบเครื่องจริง ให้ใช้ IP ของคอมพิวเตอร์คุณ เช่น http://192.168.1.xxx:3001/
    private const val BASE_URL = "http://10.0.2.2:3001/"

    val apiService: TravelApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TravelApiService::class.java)
    }
}