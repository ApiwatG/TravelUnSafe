package com.example.travelunsafe

// ===== API GENERIC RESPONSE =====
data class ApiResponse(
    val error: Boolean? = false,
    val message: String? = null
)

// ===== USER =====
data class User(
    val user_id: String,
    val username: String,
    val email: String,
    val image_profile: String? = null,
    val role: String = "user",
    val createdAt: String? = null
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String,
    val role: String = "user"
)

data class RegisterResponse(
    val message: String,
    val user_id: String? = null
)

// ===== TRIP =====
data class Trip(
    val trip_id: String,
    val trip_name: String,
    val start_date: String? = null,
    val end_date: String? = null,
    val user_id: String,
    val createdAt: String? = null
)

data class CreateTripRequest(
    val trip_name: String,
    val start_date: String? = null,
    val end_date: String? = null,
    val user_id: String
)

data class CreateTripResponse(
    val message: String,
    val trip_id: String? = null
)

// ===== HOTEL =====
data class Hotel(
    val hotel_id: String,
    val hotel_name: String,
    val address: String? = null,
    val province: String? = null,
    val price_per_night: Int = 0,
    val max_guest: Int = 1,
    val contact_phone: String? = null,
    val image_url: String? = null,
    val provinces_id: String? = null
)

// ===== PLACE =====
data class Place(
    val place_id: String,
    val place_name: String,
    val location: String? = null,
    val view: Int = 0,
    val category_id: String? = null,
    val provinces_id: String? = null
)

// ===== PROVINCE =====
data class Province(
    val provinces_id: String,
    val provinces_name: String
)

// ===== BOOKING =====
data class Booking(
    val booking_id: String,
    val booking_date: String? = null,
    val check_in_date: String,
    val check_out_date: String,
    val total_price: Int = 0,
    val status: String = "pending",
    val hotel_id: String,
    val trip_id: String? = null,
    val user_id: String,
    // joined fields from server
    val hotel_name: String? = null,
    val hotel_address: String? = null,
    val image_url: String? = null
)

data class CreateBookingRequest(
    val hotel_id: String,
    val trip_id: String? = null,
    val user_id: String,
    val check_in_date: String,
    val check_out_date: String,
    val total_price: Int = 0
)

data class CreateBookingResponse(
    val message: String,
    val booking_id: String? = null
)

// ===== REVIEW =====
data class Review(
    val review_id: String,
    val rating: Int,
    val comment: String? = null,
    val review_date: String? = null,
    val hotel_id: String,
    val user_id: String,
    // joined fields from server
    val username: String? = null,
    val image_profile: String? = null
)

data class CreateReviewRequest(
    val hotel_id: String,
    val user_id: String,
    val rating: Int,
    val comment: String? = null
)

// ===== EXPENSE =====
data class Expense(
    val expense_id: String,
    val expense_name: String,
    val amount: Int = 0,
    val expense_date: String? = null,
    val trip_id: String,
    val category_expense_id: String? = null,
    // joined field
    val category_expense_name: String? = null
)

data class CreateExpenseRequest(
    val expense_name: String,
    val amount: Int = 0,
    val expense_date: String? = null,
    val trip_id: String,
    val category_expense_id: String? = null
)

// ===== ITINERARY =====
data class Itinerary(
    val itinerary_id: String,
    val date: String? = null,
    val start_time: String? = null,
    val end_time: String? = null,
    val note: String? = null,
    val trip_id: String,
    val place_id: String,
    // joined fields
    val place_name: String? = null,
    val location: String? = null
)

// ===== CATEGORY =====
data class CategoryExpense(
    val category_expense_id: String,
    val category_expense_name: String
)

data class CategoryPlace(
    val category_place_id: String,
    val category_place_name: String
)

// ===== GUIDE =====
data class GuideModel(
    val guide_id: String,
    val guide_name: String,
    val image_guide: String? = null,
    val provinces_name: String? = null
)

// ===== PROFILE SUMMARY (returned by /profile/:id) =====
data class ProfileStats(
    val tripCount: Int = 0,
    val guideCount: Int = 0
)

data class ProfileSummary(
    val user: User,
    val stats: ProfileStats,
    val trips: List<Trip>,
    val guides: List<GuideModel>
)

// ===== FAVORITE PLACE =====
data class FavoritePlace(
    val favorite_id: String,
    val user_id: String,
    val place_id: String,
    val place_name: String,
    val location: String? = null,
    val view: Int = 0,
    val category_place_name: String? = null,
    val provinces_name: String? = null,
    val createdAt: String? = null
)

data class AddFavoriteRequest(
    val user_id: String,
    val place_id: String
)

data class AddFavoriteResponse(
    val message: String,
    val favorite_id: String? = null
)