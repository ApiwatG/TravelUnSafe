package com.example.travelunsafe

import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

// =======================================================
//  TripPlanAPI — friend's server (port 3001 / emulator 10.0.2.2)
//  Handles: hotels, places, trips, bookings, expenses, itinerary, trip members
//
//  ⚠️  This is SEPARATE from TravelAPI (port 3000) which handles
//      auth, friends, profile, favorites.
// =======================================================

interface TripPlanApiService {

    @GET("provinces")
    suspend fun getAllProvinces(): List<Province>

    // ── USER ──────────────────────────────────────────────
    @POST("api/login")
    suspend fun login(@Body request: LoginRequest): User

    @POST("api/register")
    suspend fun register(@Body request: RegisterRequest): RegisterResponse

    @GET("api/profile/{id}")
    suspend fun getProfile(@Path("id") userId: String): ProfileSummary

    // ── HOTELS ────────────────────────────────────────────
    @GET("api/hotels")
    suspend fun getHotels(): List<Hotel>

    @GET("api/hotels/{id}")
    suspend fun getHotelById(@Path("id") id: String): Hotel

    // ── PLACES ────────────────────────────────────────────
    @GET("api/places")
    suspend fun getPlaces(): List<Place>

    // ── TRIPS ─────────────────────────────────────────────
    @GET("api/trips/user/{userId}")
    suspend fun getUserTrips(@Path("userId") userId: String): List<Trip>

    @GET("api/trips/{id}")
    suspend fun getTripById(@Path("id") id: String): Trip

    @POST("api/trips")
    suspend fun createTrip(@Body request: CreateTripRequest): CreateTripResponse

    @PUT("api/trips/{id}")
    suspend fun updateTripName(@Path("id") tripId: String, @Body request: UpdateTripRequest): ApiResponse

    // ── TRIP MEMBERS ──────────────────────────────────────
    @GET("api/trips/{tripId}/members")
    suspend fun getTripMembers(@Path("tripId") tripId: String): List<Friend>

    @POST("api/trips/{tripId}/members")
    suspend fun addTripMember(@Path("tripId") tripId: String, @Body request: AddMemberRequest): ApiResponse

    @DELETE("api/trips/{tripId}/members/{userId}")
    suspend fun removeTripMember(@Path("tripId") tripId: String, @Path("userId") userId: String): ApiResponse

    // ── BOOKINGS ──────────────────────────────────────────
    @GET("api/bookings/user/{userId}")
    suspend fun getUserBookings(@Path("userId") userId: String): List<Booking>

    @POST("api/bookings")
    suspend fun createBooking(@Body request: CreateBookingRequest): CreateBookingResponse

    // ── ITINERARY ─────────────────────────────────────────
    @GET("api/itinerarys/trip/{tripId}")
    suspend fun getItineraryByTrip(@Path("tripId") tripId: String): List<Itinerary>

    @POST("api/itinerarys")
    suspend fun createItinerary(@Body request: CreateItineraryRequest): ApiResponse

    // ── EXPENSES ──────────────────────────────────────────
    @GET("api/expenses/trip/{tripId}")
    suspend fun getExpensesByTrip(@Path("tripId") tripId: String): List<Expense>

    @POST("api/expenses")
    suspend fun createExpense(@Body request: CreateExpenseRequest): ApiResponse

    // ── FRIENDS (used by PlanDetailViewModel) ─────────────
    @GET("api/friends/{userId}")
    suspend fun getFriends(@Path("userId") userId: String): List<Friend>

    // ── FAVORITES ─────────────────────────────────────────
    @GET("api/favorites/user/{userId}")
    suspend fun getUserFavorites(@Path("userId") userId: String): List<FavoritePlace>

    @POST("api/favorites")
    suspend fun addFavorite(@Body request: AddFavoriteRequest): AddFavoriteResponse

    @DELETE("api/itinerarys/{id}")
    suspend fun deleteItinerary(@Path("id") itineraryId: String): Response<ApiResponse>

    @DELETE("api/expenses/{id}")
    suspend fun deleteExpense(@Path("id") expenseId: String): Response<ApiResponse>
}

object TripPlanClient {
    // Emulator → 10.0.2.2 | Real device → your LAN IP e.g. 192.168.1.x
    private const val BASE_URL = "http://192.168.1.11:3000/"

    val apiService: TripPlanApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TripPlanApiService::class.java)
    }
}