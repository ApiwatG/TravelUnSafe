package com.example.travelunsafe

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HotelViewModel : ViewModel() {
    private val _hotels = MutableStateFlow<List<Hotel>>(emptyList())
    val hotels: StateFlow<List<Hotel>> = _hotels.asStateFlow()

    // ให้เริ่มต้นที่ false เพราะเรายังไม่ได้กดค้นหา
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isFallbackMode = MutableStateFlow(false)
    val isFallbackMode: StateFlow<Boolean> = _isFallbackMode.asStateFlow()

    private val _searchedProvince = MutableStateFlow("")
    val searchedProvince: StateFlow<String> = _searchedProvince.asStateFlow()

    fun searchHotels(query: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _searchedProvince.value = query // เก็บคำค้นหาล่าสุดไว้

                // ----------------------------------------------------
                // ✅ แก้ตรงนี้: เปลี่ยนจาก HotelClient เป็น TripPlanClient
                // ----------------------------------------------------
                val allHotels = TravelClient.travelAPI.getHotels()

                if (query.isBlank()) {
                    _hotels.value = allHotels
                    _isFallbackMode.value = false
                } else {
                    val filteredHotels = allHotels.filter { hotel ->
                        (hotel.province_name?.contains(query, ignoreCase = true) == true) ||
                                (hotel.hotel_name.contains(query, ignoreCase = true)) ||
                                (hotel.address?.contains(query, ignoreCase = true) == true)
                    }

                    if (filteredHotels.isEmpty()) {
                        _hotels.value = allHotels
                        _isFallbackMode.value = true
                    } else {
                        _hotels.value = filteredHotels
                        _isFallbackMode.value = false
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun filterHotels(minPrice: Double?, maxPrice: Double?, maxGuest: Int?) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val allHotels = TravelClient.travelAPI.getHotels()
                val filtered = allHotels.filter { hotel ->
                    val priceOk = (minPrice == null || hotel.price_per_night >= minPrice) &&
                            (maxPrice == null || hotel.price_per_night <= maxPrice)
                    val guestOk = maxGuest == null || hotel.max_guest >= maxGuest
                    priceOk && guestOk
                }
                _hotels.value = filtered
                _isFallbackMode.value = filtered.isEmpty()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
}