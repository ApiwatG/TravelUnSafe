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
    private val _isLoading = MutableStateFlow(true)  // ✅ เริ่มเป็น true
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isFallbackMode = MutableStateFlow(false)
    val isFallbackMode: StateFlow<Boolean> = _isFallbackMode.asStateFlow()

    private val _searchedProvince = MutableStateFlow("")
    val searchedProvince: StateFlow<String> = _searchedProvince.asStateFlow()

    private val _initialProvince = MutableStateFlow("")
    val initialProvince: StateFlow<String> = _initialProvince.asStateFlow()

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

    fun filterHotels(minPrice: Double?, maxPrice: Double?, maxGuest: Int?, minRating: Double?) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val allHotels = TravelClient.travelAPI.getHotels()

                // ✅ ถ้ามี minRating ให้ดึง reviews มาคำนวณ average rating ต่อโรงแรม
                val ratingMap: Map<String, Double> = if (minRating != null) {
                    val allReviews = TravelClient.travelAPI.getReviews()
                    allReviews
                        .groupBy { it.hotel_id }
                        .mapValues { (_, reviews) -> reviews.map { it.rating }.average() }
                } else emptyMap()

                val filtered = allHotels.filter { hotel ->
                    val priceOk = (minPrice == null || hotel.price_per_night >= minPrice) &&
                            (maxPrice == null || hotel.price_per_night <= maxPrice)
                    val guestOk = maxGuest == null || hotel.max_guest >= maxGuest
                    val ratingOk = minRating == null ||
                            (ratingMap[hotel.hotel_id] ?: 0.0) >= minRating
                    priceOk && guestOk && ratingOk
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
    fun setInitialProvince(province: String) {
        _initialProvince.value = province
        searchHotels(province) // โหลดโรงแรมในจังหวัดนั้นทันที
    }

}