package com.example.travelunsafe

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Fetches hotels from the friend's server (port 3001 via TripPlanClient)
class HotelViewModel : ViewModel() {

    private val _hotels = MutableStateFlow<List<Hotel>>(emptyList())
    val hotels: StateFlow<List<Hotel>> = _hotels.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        fetchHotels()
    }

    private fun fetchHotels() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                // ✅ Changed from RetrofitClient (deleted) → TripPlanClient (port 3001)
                val response = TripPlanClient.apiService.getHotels()
                _hotels.value = response
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
}