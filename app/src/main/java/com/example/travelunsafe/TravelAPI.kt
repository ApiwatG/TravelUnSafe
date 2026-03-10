package com.example.travelunsafe

import retrofit2.Response
import retrofit2.http.*

interface TravelAPI {

    interface FavoriteApiService {
        // Get all favorites for a user (for Favorites screen / initial load)
        @GET("favorites/user/{userId}")
        suspend fun getUserFavorites(
            @Path("userId") userId: String
        ): Response<FavResponse<List<FavoritePlace>>>

        // Toggle favorite on/off — returns new state
        @POST("favorites/toggle")
        suspend fun toggleFavorite(
            @Body request: FavoriteRequest
        ): Response<FavResponse<FavoriteToggleResponse>>

        // Check if a single place is favorited (optional — isFavorite in /places already covers this)
        @GET("favorites/check")
        suspend fun checkFavorite(
            @Query("user_id") userId: String,
            @Query("place_id") placeId: String
        ): Response<FavResponse<FavoriteCheckResponse>>
    }



    @GET("provinces")
    suspend fun getAllProvinces(): List<Province>

    @POST("login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>

    // ===================================
    //  IMAGE UPLOAD
    // ===================================

    @Multipart
    @POST("upload/profile")
    suspend fun uploadProfileImage(
        @Query("user_id") userId: String,
        @Part image: okhttp3.MultipartBody.Part
    ): Response<UploadResponse>

    // ===================================
    //  FRIENDS
    // ===================================

    @GET("friends/{user_id}")
    suspend fun getFriends(
        @Path("user_id") userId: String
    ): Response<FriendListResponse>

    @POST("friends/request")
    suspend fun sendFriendRequest(
        @Body request: SendFriendRequest
    ): Response<ApiResponse>

    @PUT("friends/{id}/accept")
    suspend fun acceptFriendRequest(
        @Path("id") friendshipId: String
    ): Response<ApiResponse>

    @PUT("friends/{id}/decline")
    suspend fun declineFriendRequest(
        @Path("id") friendshipId: String
    ): Response<ApiResponse>

    @DELETE("friends/{id}")
    suspend fun unfriend(
        @Path("id") friendshipId: String
    ): Response<ApiResponse>

    // ===================================
    //  GUIDES
    // ===================================

    @GET("guides")
    suspend fun getGuides(): List<GuideModel>

    @GET("guides/{guideId}/posts")
    suspend fun getGuidePosts(
        @Path("guideId") guideId: String
    ): List<GuidePost>

    @POST("guides/{guideId}/posts")
    suspend fun createGuidePost(
        @Path("guideId") guideId: String,
        @Body body: CreateGuidePostRequest
    ): Response<CreateGuidePostResponse>

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

    @GET("api/hotels")
    suspend fun getHotels(): List<Hotel>

    // ===================================
    //  PLACES
    // ===================================

    @GET("places")
    suspend fun getAllPlaces(
        @Query("provinces_id") provincesId: String? = null,
        @Query("category_id") categoryId: String? = null
    ): List<Place>

    @GET("places/{id}")
    suspend fun getPlaceById(
        @Path("id") placeId: String
    ): Response<PlaceDetail>

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

    @DELETE("api/expenses/{id}")
    suspend fun deleteExpense(@Path("id") expenseId: String): Response<ApiResponse>

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

    @GET("api/users/{userId}/trip-invitations")
    suspend fun getTripInvitations(@Path("userId") userId: String): retrofit2.Response<List<TripInvitation>>

    @PUT("api/trips/{tripId}/members/{userId}/accept")
    suspend fun acceptTripInvitation(
        @Path("tripId") tripId: String,
        @Path("userId") userId: String
    ): retrofit2.Response<ApiResponse>

    // เพิ่มคำสั่งสำหรับปฏิเสธคำเชิญ (ลบคนออกจากทริป)
    @DELETE("api/trips/{tripId}/members/{userId}")
    suspend fun deleteMemberFromTrip(
        @Path("tripId") tripId: String,
        @Path("userId") userId: String
    ): retrofit2.Response<ApiResponse>



    @DELETE("api/itinerarys/{id}")
    suspend fun deleteItinerary(@Path("id") itineraryId: String): Response<ApiResponse>

    // ===================================
    //  GUIDE VIEW COUNT
    // ===================================

    @PATCH("guides/{id}/view")
    suspend fun incrementGuideView(@Path("id") guideId: String): Response<ApiResponse>

    // ===================================
    //  FAVORITE GUIDES
    // ===================================

    @GET("favorite-guides")
    suspend fun getFavoriteGuides(
        @Query("user_id") userId: String
    ): Response<List<FavoriteGuide>>

    @POST("favorite-guides")
    suspend fun addFavoriteGuide(
        @Body request: AddFavoriteGuideRequest
    ): Response<AddFavoriteGuideResponse>

    @DELETE("favorite-guides/{id}")
    suspend fun removeFavoriteGuide(
        @Path("id") favoriteGuideId: String
    ): Response<ApiResponse>

    @GET("favorite-guides/check")
    suspend fun checkFavoriteGuide(
        @Query("user_id") userId: String,
        @Query("guide_id") guideId: String
    ): Response<FavoriteGuideCheckResponse>

    @DELETE("guides/delete/{id}")
    suspend fun deleteGuide(@Path("id") guideId: String): Response<ApiResponse>

    // ===================================
    //  GUIDES (เพิ่มส่วนนี้เข้าไปครับ)
    // ===================================

    @Multipart
    @PUT("guides/{id}")
    suspend fun updateGuide(
        @Path("id") guideId: String,
        @Part("title") title: okhttp3.RequestBody?,
        @Part("description") description: okhttp3.RequestBody?,
        @Part image: okhttp3.MultipartBody.Part?
    ): Response<ApiResponse>
}