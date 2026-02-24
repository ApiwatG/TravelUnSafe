package com.example.travelunsafe

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class TravelViewModel : ViewModel() {

    // ตัวแปรเก็บรายการโรงแรมและสถานที่
    var hotelList by mutableStateOf<List<Hotel>>(emptyList())
        private set

    var placeList by mutableStateOf<List<Place>>(emptyList())
        private set

    // ตัวแปรเก็บสถานะการโหลดและข้อผิดพลาด
    var isLoading by mutableStateOf(false)
        private set
    var errorMessage by mutableStateOf("")
        private set

    init {
        // ให้ดึงข้อมูลทันทีที่เปิดแอป
        fetchHotels()
        fetchPlaces()
    }

    private fun fetchHotels() {
        viewModelScope.launch {
            isLoading = true
            try {
                val response = TravelApiClient.apiService.getHotels()
                hotelList = response
            } catch (e: Exception) {
                errorMessage = "Error fetching hotels: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    private fun fetchPlaces() {
        viewModelScope.launch {
            isLoading = true
            try {
                val response = TravelApiClient.apiService.getPlaces()
                placeList = response
            } catch (e: Exception) {
                errorMessage = "Error fetching places: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }
}