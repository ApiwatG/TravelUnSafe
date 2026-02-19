package com.example.travelunsafe

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HotelViewModel : ViewModel() {
    // เก็บรายการโรงแรม
    private val _hotels = MutableStateFlow<List<Hotel>>(emptyList())
    val hotels: StateFlow<List<Hotel>> = _hotels.asStateFlow()

    // เก็บสถานะการโหลด (หมุนๆ)
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        fetchHotels() // สั่งโหลดข้อมูลทันทีที่เปิดหน้าจอ
    }

    private fun fetchHotels() {
        viewModelScope.launch {
            try {
                _isLoading.value = true // เริ่มโหลด (โชว์ Skeleton)
                // ยิง API ไปที่ Server
                val response = RetrofitClient.apiService.getHotels()
                _hotels.value = response // เอาข้อมูลที่ได้มาเก็บไว้
            } catch (e: Exception) {
                e.printStackTrace()
                // ถ้า Server ปิด หรือพัง จะตกมาที่นี่
                println("Error fetching data: ${e.message}")
            } finally {
                _isLoading.value = false // โหลดเสร็จแล้ว (ปิด Skeleton)
            }
        }
    }
}