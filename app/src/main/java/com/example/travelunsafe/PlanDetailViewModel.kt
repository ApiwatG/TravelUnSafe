package com.example.travelunsafe

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class PlanDetailViewModel : ViewModel() {

    var currentTrip     by mutableStateOf<Trip?>(null)
    var itineraryList   by mutableStateOf<List<Itinerary>>(emptyList())
    var expenseList     by mutableStateOf<List<Expense>>(emptyList())
    var availablePlaces by mutableStateOf<List<Place>>(emptyList())
    var friendsList     by mutableStateOf<List<Friend>>(emptyList())
    var tripMembers     by mutableStateOf<List<Friend>>(emptyList())
    var isLoading       by mutableStateOf(false)
    var errorMessage    by mutableStateOf<String?>(null)

    var tripBookings by mutableStateOf<List<Booking>>(emptyList())
        private set

    val totalExpense: Int
        get() = expenseList.sumOf { it.amount }

    // 💡 รวบตึงเหลือฟังก์ชันเดียว และดึงข้อมูลโรงแรม (tripBookings) เข้ามาแล้ว
    fun loadTripDetail(tripId: String, userId: String) {
        viewModelScope.launch {
            isLoading = true
            try { currentTrip    = TripPlanClient.apiService.getTripById(tripId)           } catch (_: Exception) {}
            try { tripBookings   = TripPlanClient.apiService.getBookingsByTrip(tripId)     } catch (_: Exception) { tripBookings = emptyList() }
            try { itineraryList  = TripPlanClient.apiService.getItineraryByTrip(tripId)    } catch (_: Exception) { itineraryList  = emptyList() }
            try { expenseList    = TripPlanClient.apiService.getExpensesByTrip(tripId)     } catch (_: Exception) { expenseList    = emptyList() }
            try { availablePlaces = TripPlanClient.apiService.getPlaces()                  } catch (_: Exception) { availablePlaces = emptyList() }
            try { friendsList    = TripPlanClient.apiService.getFriends(userId)            } catch (_: Exception) { friendsList    = emptyList() }
            try { tripMembers    = TripPlanClient.apiService.getTripMembers(tripId)        } catch (_: Exception) { tripMembers    = emptyList() }
            isLoading = false
        }
    }

    fun addExpense(tripId: String, name: String, amount: Int, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                TripPlanClient.apiService.createExpense(CreateExpenseRequest(expense_name = name, amount = amount, trip_id = tripId))
                expenseList = TripPlanClient.apiService.getExpensesByTrip(tripId)
                onSuccess()
            } catch (e: Exception) { onError(e.message ?: "Error") }
        }
    }

    fun addItinerary(tripId: String, placeId: String, startTime: String, endTime: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                TripPlanClient.apiService.createItinerary(CreateItineraryRequest(trip_id = tripId, place_id = placeId, start_time = startTime, end_time = endTime))
                itineraryList = TripPlanClient.apiService.getItineraryByTrip(tripId)
                onSuccess()
            } catch (e: Exception) { onError(e.message ?: "Error") }
        }
    }

    fun editTripName(tripId: String, newName: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                TripPlanClient.apiService.updateTripName(tripId, UpdateTripRequest(trip_name = newName))
                currentTrip = TripPlanClient.apiService.getTripById(tripId)
                onSuccess()
            } catch (e: Exception) { onError(e.message ?: "Error") }
        }
    }

    fun addMemberToTrip(tripId: String, userId: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                TripPlanClient.apiService.addTripMember(tripId, AddMemberRequest(user_id = userId))
                tripMembers = TripPlanClient.apiService.getTripMembers(tripId)
                onSuccess()
            } catch (e: Exception) { onError(e.message ?: "Error") }
        }
    }

    fun removeMemberFromTrip(tripId: String, userId: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                TripPlanClient.apiService.removeTripMember(tripId, userId)
                tripMembers = TripPlanClient.apiService.getTripMembers(tripId)
                onSuccess()
            } catch (e: Exception) { onError(e.message ?: "Error") }
        }
    }

    fun deleteItinerary(tripId: String, itineraryId: String) {
        viewModelScope.launch {
            try {
                val response = TripPlanClient.apiService.deleteItinerary(itineraryId)
                if (response.isSuccessful) {
                    itineraryList = TripPlanClient.apiService.getItineraryByTrip(tripId)
                }
            } catch (e: Exception) { e.printStackTrace() }
        }
    }

    fun deleteExpense(tripId: String, expenseId: String) {
        viewModelScope.launch {
            try {
                val response = TripPlanClient.apiService.deleteExpense(expenseId)
                if (response.isSuccessful) {
                    expenseList = TripPlanClient.apiService.getExpensesByTrip(tripId)
                }
            } catch (e: Exception) { e.printStackTrace() }
        }
    }

    fun deleteBooking(tripId: String, bookingId: String) {
        viewModelScope.launch {
            try {
                // 1. ส่งคำสั่งไปลบที่เซิร์ฟเวอร์
                TripPlanClient.apiService.deleteBooking(bookingId)

                // 2. 💡 สั่งดึงข้อมูลโรงแรมของทริปนี้ใหม่จากฐานข้อมูลมาอัปเดตหน้าจอทันที
                // (ถ้าลบสำเร็จ ข้อมูลใน List จะหายไปทันทีเหมือนของสถานที่และค่าใช้จ่ายครับ)
                tripBookings = TripPlanClient.apiService.getBookingsByTrip(tripId)

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}