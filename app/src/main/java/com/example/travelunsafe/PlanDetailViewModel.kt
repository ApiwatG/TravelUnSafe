package com.example.travelunsafe

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class PlanDetailViewModel : ViewModel() {

    var currentTrip by mutableStateOf<Trip?>(null)
    var itineraryList by mutableStateOf<List<Itinerary>>(emptyList())
    var expenseList by mutableStateOf<List<Expense>>(emptyList())
    var availablePlaces by mutableStateOf<List<Place>>(emptyList())
    var friendsList by mutableStateOf<List<Friend>>(emptyList()) // เพื่อนทั้งหมด (ไว้โชว์ในช่อง Search)

    // 💡 ตัวแปรใหม่ เก็บสมาชิกที่อยู่ในทริปนี้จริงๆ
    var tripMembers by mutableStateOf<List<Friend>>(emptyList())

    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    val totalExpense: Int
        get() = expenseList.sumOf { it.amount }

    fun loadTripDetail(tripId: String) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null

            try { currentTrip = TravelApiClient.apiService.getTripById(tripId) } catch (e: Exception) { }
            try { itineraryList = TravelApiClient.apiService.getItineraryByTrip(tripId) } catch (e: Exception) { itineraryList = emptyList() }
            try { expenseList = TravelApiClient.apiService.getExpensesByTrip(tripId) } catch (e: Exception) { expenseList = emptyList() }
            try { availablePlaces = TravelApiClient.apiService.getPlaces() } catch (e: Exception) { availablePlaces = emptyList() }
            try { friendsList = TravelApiClient.apiService.getFriends("U0001") } catch (e: Exception) { friendsList = emptyList() }

            // 💡 โหลดรายชื่อ "คนที่อยู่ในทริป"
            try {
                tripMembers = TravelApiClient.apiService.getTripMembers(tripId)
            } catch (e: Exception) {
                tripMembers = emptyList()
            }

            isLoading = false
        }
    }

    // ... (ฟังก์ชัน addExpense, addItinerary, editTripName ใช้ของเดิมได้เลย) ...
    fun addExpense(tripId: String, name: String, amount: Int, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                TravelApiClient.apiService.createExpense(CreateExpenseRequest(expense_name = name, amount = amount, trip_id = tripId))
                expenseList = TravelApiClient.apiService.getExpensesByTrip(tripId)
                onSuccess()
            } catch (e: Exception) { onError(e.message ?: "Error") }
        }
    }

    fun addItinerary(tripId: String, placeId: String, startTime: String, endTime: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                TravelApiClient.apiService.createItinerary(CreateItineraryRequest(trip_id = tripId, place_id = placeId, start_time = startTime, end_time = endTime))
                itineraryList = TravelApiClient.apiService.getItineraryByTrip(tripId)
                onSuccess()
            } catch (e: Exception) { onError(e.message ?: "Error") }
        }
    }

    fun editTripName(tripId: String, newName: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                TravelApiClient.apiService.updateTripName(tripId, UpdateTripRequest(trip_name = newName))
                currentTrip = TravelApiClient.apiService.getTripById(tripId)
                onSuccess()
            } catch (e: Exception) { onError(e.message ?: "Error") }
        }
    }

    // 💡 ฟังก์ชันแอดเพื่อนเข้าทริป
    fun addMemberToTrip(tripId: String, userId: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                TravelApiClient.apiService.addTripMember(tripId, AddMemberRequest(user_id = userId))
                tripMembers = TravelApiClient.apiService.getTripMembers(tripId) // โหลดรายชื่อใหม่
                onSuccess()
            } catch (e: Exception) { onError(e.message ?: "Error") }
        }
    }

    // 💡 ฟังก์ชันเตะเพื่อนออก
    fun removeMemberFromTrip(tripId: String, userId: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                TravelApiClient.apiService.removeTripMember(tripId, userId)
                tripMembers = TravelApiClient.apiService.getTripMembers(tripId) // โหลดรายชื่อใหม่
                onSuccess()
            } catch (e: Exception) { onError(e.message ?: "Error") }
        }
    }
}