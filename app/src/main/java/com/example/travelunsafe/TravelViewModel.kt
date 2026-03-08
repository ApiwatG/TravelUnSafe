package com.example.travelunsafe

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

class TravelViewModel : ViewModel() {

    // ===== STATE =====

    // Auth
    var currentUser by mutableStateOf<User?>(null)
        private set

    // Lists
    var trips by mutableStateOf<List<Trip>>(emptyList())
        private set

    var hotels by mutableStateOf<List<Hotel>>(emptyList())
        private set

    var places by mutableStateOf<List<Place>>(emptyList())
        private set

    var provinces by mutableStateOf<List<Province>>(emptyList())
        private set

    var bookings by mutableStateOf<List<Booking>>(emptyList())
        private set

    var reviews by mutableStateOf<List<Review>>(emptyList())
        private set

    var expenses by mutableStateOf<List<Expense>>(emptyList())
        private set

    var itinerary by mutableStateOf<List<Itinerary>>(emptyList())
        private set

    var favorites by mutableStateOf<List<FavoritePlace>>(emptyList())
        private set

    var guides by mutableStateOf<List<GuideModel>>(emptyList())
        private set

    // Friends
    var friends by mutableStateOf<List<FriendItem>>(emptyList())
        private set
    var receivedRequests by mutableStateOf<List<FriendRequest>>(emptyList())
        private set
    var sentRequests by mutableStateOf<List<FriendRequest>>(emptyList())
        private set

    // Profile summary
    var profileSummary by mutableStateOf<ProfileSummary?>(null)
        private set

    // UI state
    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf("")
        private set

    var successMessage by mutableStateOf("")
        private set

    // ===== HELPERS =====

    fun clearMessages() {
        errorMessage = ""
        successMessage = ""
    }

    // ===================================
    //  AUTH — login + register + load user
    // ===================================

    fun login(
        email: String,
        password: String,
        prefs: SharedPreferencesManager,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            isLoading = true
            try {
                val response = TravelClient.travelAPI.login(LoginRequest(email, password))
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null && !body.error) {
                        prefs.saveLoginStatus(
                            isLoggedIn = true,
                            userId     = body.user_id ?: "",
                            username   = body.username ?: "",
                            email      = body.email ?: email,
                            role       = body.role ?: "user"
                        )
                        onSuccess()
                    } else {
                        onError(body?.message ?: "อีเมลหรือรหัสผ่านไม่ถูกต้อง")
                    }
                } else {
                    onError("อีเมลหรือรหัสผ่านไม่ถูกต้อง")
                }
            } catch (e: Exception) {
                onError("ไม่สามารถเชื่อมต่อ Server ได้")
            } finally {
                isLoading = false
            }
        }
    }

    fun register(
        context: Context,
        username: String,
        email: String,
        password: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            isLoading = true
            try {
                val response = TravelClient.travelAPI.createUser(
                    RegisterRequest(username = username, email = email, password = password)
                )
                if (response.isSuccessful) {
                    Toast.makeText(context, "สมัครสมาชิกสำเร็จ!", Toast.LENGTH_LONG).show()
                    onSuccess()
                } else {
                    // Parse actual error body (not HTTP phrase)
                    val errorBody = response.errorBody()?.string()
                    val msg = try {
                        org.json.JSONObject(errorBody ?: "").optString("message", response.message())
                    } catch (e: Exception) {
                        response.message()
                    }
                    errorMessage = "สมัครสมาชิกไม่สำเร็จ: $msg"
                    Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                errorMessage = "ไม่สามารถเชื่อมต่อ Server ได้: ${e.message}"
                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
            } finally {
                isLoading = false
            }
        }
    }

    fun loadUserById(userId: String) {
        viewModelScope.launch {
            isLoading = true
            try {
                val response = TravelClient.travelAPI.getUserById(userId)
                if (response.isSuccessful) {
                    currentUser = response.body()
                    errorMessage = ""
                } else {
                    errorMessage = "User not found"
                }
            } catch (e: Exception) {
                errorMessage = "Error: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun logout() {
        currentUser = null
        trips = emptyList()
        bookings = emptyList()
    }

    // ===================================
    //  TRIPS
    // ===================================

    fun loadTrips(userId: String? = null) {
        viewModelScope.launch {
            isLoading = true
            try {
                trips = TravelClient.travelAPI.getAllTrips(userId)
                errorMessage = ""
            } catch (e: Exception) {
                errorMessage = "Failed to load trips: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun createTrip(
        context: Context,
        tripName: String,
        startDate: String?,
        endDate: String?,
        userId: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            isLoading = true
            try {
                val response = TravelClient.travelAPI.createTrip(
                    CreateTripRequest(
                        trip_name = tripName,
                        start_date = startDate,
                        end_date = endDate,
                        user_id = userId
                    )
                )
                if (response.isSuccessful) {
                    Toast.makeText(context, "Trip created!", Toast.LENGTH_SHORT).show()
                    loadTrips(userId)  // refresh list
                    onSuccess()
                } else {
                    errorMessage = "Failed to create trip: ${response.message()}"
                }
            } catch (e: Exception) {
                errorMessage = "Network error: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun deleteTrip(tripId: String, userId: String) {
        viewModelScope.launch {
            try {
                val response = TravelClient.travelAPI.deleteTrip(tripId)
                if (response.isSuccessful) {
                    loadTrips(userId)  // refresh
                } else {
                    errorMessage = "Failed to delete trip"
                }
            } catch (e: Exception) {
                errorMessage = "Error: ${e.message}"
            }
        }
    }

    // ===================================
    //  HOTELS
    // ===================================

    fun loadHotels(provincesId: String? = null) {
        viewModelScope.launch {
            isLoading = true
            try {
                hotels = TravelClient.travelAPI.getAllHotels(provincesId)
                errorMessage = ""
            } catch (e: Exception) {
                errorMessage = "Failed to load hotels: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    // ===================================
    //  PLACES
    // ===================================

    fun loadPlaces(provincesId: String? = null, categoryId: String? = null) {
        viewModelScope.launch {
            isLoading = true
            try {
                places = TravelClient.travelAPI.getAllPlaces(provincesId, categoryId)
                errorMessage = ""
            } catch (e: Exception) {
                errorMessage = "Failed to load places: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    // ===================================
    //  PROVINCES
    // ===================================

    fun loadProvinces() {
        viewModelScope.launch {
            try {
                provinces = TravelClient.travelAPI.getAllProvinces()
            } catch (e: Exception) {
                errorMessage = "Failed to load provinces: ${e.message}"
            }
        }
    }

    // ===================================
    //  BOOKINGS
    // ===================================

    fun loadBookings(userId: String? = null) {
        viewModelScope.launch {
            isLoading = true
            try {
                bookings = TravelClient.travelAPI.getBookings(userId)
                errorMessage = ""
            } catch (e: Exception) {
                errorMessage = "Failed to load bookings: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun createBooking(
        context: Context,
        hotelId: String,
        userId: String,
        tripId: String?,
        checkIn: String,
        checkOut: String,
        totalPrice: Int,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            isLoading = true
            try {
                val response = TravelClient.travelAPI.createBooking(
                    CreateBookingRequest(
                        hotel_id = hotelId,
                        user_id = userId,
                        trip_id = tripId,
                        check_in_date = checkIn,
                        check_out_date = checkOut,
                        total_price = totalPrice
                    )
                )
                if (response.isSuccessful) {
                    Toast.makeText(context, "Booking confirmed!", Toast.LENGTH_SHORT).show()
                    loadBookings(userId)
                    onSuccess()
                } else {
                    errorMessage = "Booking failed: ${response.message()}"
                }
            } catch (e: Exception) {
                errorMessage = "Network error: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    // ===================================
    //  REVIEWS
    // ===================================

    fun loadReviews(hotelId: String? = null) {
        viewModelScope.launch {
            try {
                reviews = TravelClient.travelAPI.getReviews(hotelId)
                errorMessage = ""
            } catch (e: Exception) {
                errorMessage = "Failed to load reviews: ${e.message}"
            }
        }
    }

    fun createReview(
        context: Context,
        hotelId: String,
        userId: String,
        rating: Int,
        comment: String?,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = TravelClient.travelAPI.createReview(
                    CreateReviewRequest(
                        hotel_id = hotelId,
                        user_id = userId,
                        rating = rating,
                        comment = comment
                    )
                )
                if (response.isSuccessful) {
                    Toast.makeText(context, "Review submitted!", Toast.LENGTH_SHORT).show()
                    loadReviews(hotelId)
                    onSuccess()
                } else {
                    errorMessage = "Failed to submit review"
                }
            } catch (e: Exception) {
                errorMessage = "Error: ${e.message}"
            }
        }
    }

    // ===================================
    //  EXPENSES
    // ===================================

    fun loadExpenses(tripId: String) {
        viewModelScope.launch {
            try {
                expenses = TravelClient.travelAPI.getExpenses(tripId)
                errorMessage = ""
            } catch (e: Exception) {
                errorMessage = "Failed to load expenses: ${e.message}"
            }
        }
    }

    fun addExpense(
        context: Context,
        tripId: String,
        expenseName: String,
        amount: Int,
        date: String?,
        categoryId: String?,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = TravelClient.travelAPI.createExpense(
                    CreateExpenseRequest(
                        expense_name = expenseName,
                        amount = amount,
                        expense_date = date,
                        trip_id = tripId,
                        category_expense_id = categoryId
                    )
                )
                if (response.isSuccessful) {
                    Toast.makeText(context, "Expense added!", Toast.LENGTH_SHORT).show()
                    loadExpenses(tripId)
                    onSuccess()
                } else {
                    errorMessage = "Failed to add expense"
                }
            } catch (e: Exception) {
                errorMessage = "Error: ${e.message}"
            }
        }
    }

    // ===================================
    //  PROFILE SUMMARY
    // ===================================

    fun loadProfile(userId: String) {
        viewModelScope.launch {
            isLoading = true
            try {
                val response = TravelClient.travelAPI.getProfile(userId)
                android.util.Log.d("PROFILE_DEBUG", "loadProfile response: code=${response.code()}, body=${response.body()}")
                if (response.isSuccessful) {
                    profileSummary = response.body()
                    errorMessage = ""
                } else {
                    errorMessage = "Failed to load profile"
                    android.util.Log.d("PROFILE_DEBUG", "loadProfile error: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                android.util.Log.d("PROFILE_DEBUG", "loadProfile exception: ${e.message}")
                errorMessage = "Error: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    // ===================================
    //  FAVORITE PLACES
    // ===================================

    fun loadFavorites(userId: String) {
        viewModelScope.launch {
            isLoading = true
            try {
                favorites = TravelClient.travelAPI.getFavorites(userId)
                errorMessage = ""
            } catch (e: Exception) {
                errorMessage = "Failed to load favorites: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun addFavorite(context: Context, userId: String, placeId: String, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            try {
                val response = TravelClient.travelAPI.addFavorite(
                    AddFavoriteRequest(user_id = userId, place_id = placeId)
                )
                if (response.isSuccessful) {
                    Toast.makeText(context, "เพิ่มในรายการโปรดแล้ว", Toast.LENGTH_SHORT).show()
                    loadFavorites(userId)
                    onSuccess()
                } else {
                    errorMessage = "Failed to add favorite"
                }
            } catch (e: Exception) {
                errorMessage = "Error: ${e.message}"
            }
        }
    }

    fun removeFavorite(context: Context, favoriteId: String, userId: String) {
        viewModelScope.launch {
            try {
                val response = TravelClient.travelAPI.removeFavorite(favoriteId)
                if (response.isSuccessful) {
                    Toast.makeText(context, "ลบออกจากรายการโปรดแล้ว", Toast.LENGTH_SHORT).show()
                    loadFavorites(userId)
                } else {
                    errorMessage = "Failed to remove favorite"
                }
            } catch (e: Exception) {
                errorMessage = "Error: ${e.message}"
            }
        }
    }

    // ===================================
    //  GUIDES
    // ===================================

    fun loadGuides() {
        viewModelScope.launch {
            try {
                guides = TravelClient.travelAPI.getGuides()
                errorMessage = ""
            } catch (e: Exception) {
                errorMessage = "Failed to load guides: ${e.message}"
            }
        }
    }

    // ===================================
    //  ITINERARY
    // ===================================

    fun loadItinerary(tripId: String) {
        viewModelScope.launch {
            try {
                itinerary = TravelClient.travelAPI.getItinerary(tripId)
                errorMessage = ""
            } catch (e: Exception) {
                errorMessage = "Failed to load itinerary: ${e.message}"
            }
        }
    }

    // ===================================
    //  PROFILE IMAGE UPLOAD
    // ===================================

    fun uploadProfileImage(
        context: Context,
        userId: String,
        imageUri: android.net.Uri,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            isLoading = true
            try {
                val stream = context.contentResolver.openInputStream(imageUri)
                    ?: return@launch onError("Cannot read image")
                val bytes = stream.readBytes()
                stream.close()
                val mimeType = context.contentResolver.getType(imageUri) ?: "image/jpeg"
                val ext = mimeType.substringAfter("/")
                val requestBody = bytes.toRequestBody(mimeType.toMediaType())
                val part = MultipartBody.Part.createFormData("image", "profile.$ext", requestBody)
                val response = TravelClient.travelAPI.uploadProfileImage(userId, part)
                if (response.isSuccessful && response.body()?.error == false) {
                    onSuccess(response.body()?.image_profile ?: "")
                } else {
                    val errBody = response.errorBody()?.string()
                    val msg = try { org.json.JSONObject(errBody ?: "").optString("message") } catch (e: Exception) { response.message() }
                    onError(msg)
                }
            } catch (e: Exception) {
                onError("Network error: ${e.message}")
            } finally {
                isLoading = false
            }
        }
    }

    // ===================================
    //  FRIENDS
    // ===================================

    fun loadFriends(userId: String) {
        viewModelScope.launch {
            try {
                val response = TravelClient.travelAPI.getFriends(userId)
                if (response.isSuccessful) {
                    val body = response.body()
                    friends = (body?.friends ?: emptyList()).filter { it.status == "accepted" }
                    receivedRequests = (body?.receivedRequests ?: emptyList()).filter { it.status == "pending" }
                    sentRequests = (body?.sentRequests ?: emptyList()).filter { it.status == "pending" }
                }
            } catch (e: Exception) {
                errorMessage = "Failed to load friends: ${e.message}"
            }
        }
    }

    fun sendFriendRequest(
        requesterId: String,
        recipientEmail: String,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            isLoading = true
            try {
                val response = TravelClient.travelAPI.sendFriendRequest(
                    SendFriendRequest(requester_id = requesterId, recipient_email = recipientEmail)
                )
                if (response.isSuccessful) {
                    onSuccess(response.body()?.message ?: "ส่งคำขอแล้ว")
                    loadFriends(requesterId)
                } else {
                    val errBody = response.errorBody()?.string()
                    val msg = try { org.json.JSONObject(errBody ?: "").optString("message") } catch (e: Exception) { response.message() }
                    onError(msg)
                }
            } catch (e: Exception) {
                onError("Network error: ${e.message}")
            } finally {
                isLoading = false
            }
        }
    }

    fun acceptFriendRequest(userId: String, friendshipId: String) {
        viewModelScope.launch {
            try { TravelClient.travelAPI.acceptFriendRequest(friendshipId) } catch (_: Exception) {}
            loadFriends(userId)
        }
    }

    fun declineFriendRequest(userId: String, friendshipId: String) {
        viewModelScope.launch {
            try { TravelClient.travelAPI.declineFriendRequest(friendshipId) } catch (_: Exception) {}
            loadFriends(userId)
        }
    }

    fun unfriend(context: Context, userId: String, friendshipId: String) {
        viewModelScope.launch {
            try {
                TravelClient.travelAPI.unfriend(friendshipId)
                Toast.makeText(context, "ยกเลิกการเป็นเพื่อนแล้ว", Toast.LENGTH_SHORT).show()
            } catch (_: Exception) {}
            loadFriends(userId)
        }
    }
}

