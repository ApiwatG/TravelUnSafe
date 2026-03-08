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

    val totalExpense: Int
        get() = expenseList.sumOf { it.amount }

    fun loadTripDetail(tripId: String) {
        viewModelScope.launch {
            isLoading = true
            try { currentTrip    = TripPlanClient.apiService.getTripById(tripId)           } catch (_: Exception) {}
            try { itineraryList  = TripPlanClient.apiService.getItineraryByTrip(tripId)    } catch (_: Exception) { itineraryList  = emptyList() }
            try { expenseList    = TripPlanClient.apiService.getExpensesByTrip(tripId)     } catch (_: Exception) { expenseList    = emptyList() }
            try { availablePlaces = TripPlanClient.apiService.getPlaces()                  } catch (_: Exception) { availablePlaces = emptyList() }
            try { friendsList    = TripPlanClient.apiService.getFriends("U0001")           } catch (_: Exception) { friendsList    = emptyList() }
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
}