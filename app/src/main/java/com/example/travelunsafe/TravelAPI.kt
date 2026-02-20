package com.example.travelunsafe

import retrofit2.Response
import retrofit2.http.*

interface TravelAPI {

    // ===================================
    //  USERS
    // ===================================

    @GET("users")
    suspend fun getAllUsers(): List<User>

    @GET("users/{id}")
    suspend fun getUserById(
        @Path("id") userId: String
    ): Response<User>

    @GET("profile/{id}")
    suspend fun getProfile(
        @Path("id") userId: String
    ): Response<ProfileSummary>

    @POST("users")
    suspend fun createUser(
        @Body request: RegisterRequest
    ): Response<RegisterResponse>

    @PUT("users/{id}")
    suspend fun updateUser(
        @Path("id") userId: String,
        @Body user: User
    ): Response<ApiResponse>

    @DELETE("users/{id}")
    suspend fun deleteUser(
        @Path("id") userId: String
    ): Response<ApiResponse>

    // ===================================
    //  TRIPS
    // ===================================

    @GET("trips")
    suspend fun getAllTrips(
        @Query("user_id") userId: String? = null
    ): List<Trip>

    @GET("trips/{id}")
    suspend fun getTripById(
        @Path("id") tripId: String
    ): Response<Trip>

    @POST("trips")
    suspend fun createTrip(
        @Body request: CreateTripRequest
    ): Response<CreateTripResponse>

    @PUT("trips/{id}")
    suspend fun updateTrip(
        @Path("id") tripId: String,
        @Body trip: Trip
    ): Response<ApiResponse>

    @DELETE("trips/{id}")
    suspend fun deleteTrip(
        @Path("id") tripId: String
    ): Response<ApiResponse>

    // ===================================
    //  HOTELS
    // ===================================

    @GET("hotels")
    suspend fun getAllHotels(
        @Query("provinces_id") provincesId: String? = null
    ): List<Hotel>

    @GET("hotels/{id}")
    suspend fun getHotelById(
        @Path("id") hotelId: String
    ): Response<Hotel>

    // ===================================
    //  PLACES
    // ===================================

    @GET("places")
    suspend fun getAllPlaces(
        @Query("provinces_id") provincesId: String? = null,
        @Query("category_id") categoryId: String? = null
    ): List<Place>

    // ===================================
    //  PROVINCES
    // ===================================

    @GET("provinces")
    suspend fun getAllProvinces(): List<Province>

    // ===================================
    //  BOOKINGS
    // ===================================

    @GET("bookings")
    suspend fun getBookings(
        @Query("user_id") userId: String? = null
    ): List<Booking>

    @POST("bookings")
    suspend fun createBooking(
        @Body request: CreateBookingRequest
    ): Response<CreateBookingResponse>

    // ===================================
    //  REVIEWS
    // ===================================

    @GET("reviews")
    suspend fun getReviews(
        @Query("hotel_id") hotelId: String? = null
    ): List<Review>

    @POST("reviews")
    suspend fun createReview(
        @Body request: CreateReviewRequest
    ): Response<ApiResponse>

    // ===================================
    //  EXPENSES
    // ===================================

    @GET("expenses")
    suspend fun getExpenses(
        @Query("trip_id") tripId: String
    ): List<Expense>

    @POST("expenses")
    suspend fun createExpense(
        @Body request: CreateExpenseRequest
    ): Response<ApiResponse>

    // ===================================
    //  FAVORITE PLACES
    // ===================================

    @GET("favorites")
    suspend fun getFavorites(
        @Query("user_id") userId: String
    ): List<FavoritePlace>

    @POST("favorites")
    suspend fun addFavorite(
        @Body request: AddFavoriteRequest
    ): Response<AddFavoriteResponse>

    @DELETE("favorites/{id}")
    suspend fun removeFavorite(
        @Path("id") favoriteId: String
    ): Response<ApiResponse>

    // ===================================
    //  ITINERARY
    // ===================================

    @GET("itinerary")
    suspend fun getItinerary(
        @Query("trip_id") tripId: String
    ): List<Itinerary>
}