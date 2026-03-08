package com.example.travelunsafe

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AdminViewModel : ViewModel() {
    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users: StateFlow<List<User>> = _users

    private val _trips = MutableStateFlow<List<Trip>>(emptyList())
    val trips: StateFlow<List<Trip>> = _trips

    private val _hotels = MutableStateFlow<List<Hotel>>(emptyList())
    val hotels: StateFlow<List<Hotel>> = _hotels

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMsg = MutableStateFlow<String?>(null)
    val errorMsg: StateFlow<String?> = _errorMsg

    private val _successMsg = MutableStateFlow<String?>(null)
    val successMsg: StateFlow<String?> = _successMsg

    // --- ดึงข้อมูลจาก API ---
    fun loadUsers() {
        viewModelScope.launch {
            _isLoading.value = true
            try { _users.value = TravelClient.travelAPI.getAllUsers() }
            catch (e: Exception) { _errorMsg.value = "โหลดข้อมูล Users ไม่สำเร็จ" }
            finally { _isLoading.value = false }
        }
    }

    fun loadTrips() {
        viewModelScope.launch {
            _isLoading.value = true
            try { _trips.value = TravelClient.travelAPI.getAllTrips(null) }
            catch (e: Exception) { _errorMsg.value = "โหลดข้อมูล Trips ไม่สำเร็จ" }
            finally { _isLoading.value = false }
        }
    }

    fun loadHotels() {
        viewModelScope.launch {
            _isLoading.value = true
            try { _hotels.value = TravelClient.travelAPI.getAllHotels(null) }
            catch (e: Exception) { _errorMsg.value = "โหลดข้อมูล Hotels ไม่สำเร็จ" }
            finally { _isLoading.value = false }
        }
    }

    // --- ฟังก์ชันการลบ ---
    fun deleteUser(userId: String) {
        viewModelScope.launch {
            try {
                val response = TravelClient.travelAPI.deleteUser(userId)
                if (response.isSuccessful) {
                    _users.value = _users.value.filter { it.user_id != userId }
                    _successMsg.value = "ลบ User สำเร็จ"
                }
            } catch (e: Exception) { _errorMsg.value = "เกิดข้อผิดพลาดในการเชื่อมต่อ" }
        }
    }

    fun deleteHotel(hotelId: String) {
        viewModelScope.launch {
            try {
                val response = TravelClient.travelAPI.deleteHotel(hotelId)
                if (response.isSuccessful) {
                    _hotels.value = _hotels.value.filter { it.hotel_id != hotelId }
                    _successMsg.value = "ลบโรงแรมสำเร็จ"
                }
            } catch (e: Exception) { _errorMsg.value = "ลบโรงแรมไม่สำเร็จ" }
        }
    }

    fun deleteTrip(tripId: String) {
        viewModelScope.launch {
            try {
                val response = TravelClient.travelAPI.deleteTrip(tripId)
                if (response.isSuccessful) {
                    _trips.value = _trips.value.filter { it.trip_id != tripId }
                    _successMsg.value = "ลบ Trip สำเร็จ"
                }
            } catch (e: Exception) { _errorMsg.value = "เกิดข้อผิดพลาด" }
        }
    }

    fun clearMessages() {
        _errorMsg.value = null
        _successMsg.value = null
    }
}