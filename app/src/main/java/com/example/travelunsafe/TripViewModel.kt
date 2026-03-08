package com.example.travelunsafe

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar

class TripViewModel : ViewModel() {

    // 1. สร้าง StateFlow สำหรับเก็บรายชื่อจังหวัด
    private val _provinceList = MutableStateFlow<List<Province>>(emptyList())
    val provinceList: StateFlow<List<Province>> = _provinceList.asStateFlow()

    // 2. สั่งให้ดึงข้อมูลทันทีที่เรียกใช้ ViewModel นี้
    init {
        loadProvinces()
    }

    // ฟังก์ชันดึงข้อมูลจังหวัดจาก API
    private fun loadProvinces() {
        viewModelScope.launch {
            try {
                // เรียกใช้ API (อ้างอิงตามชื่อ TripPlanClient ของคุณ)
                val response = TripPlanClient.apiService.getAllProvinces()
                _provinceList.value = response
            } catch (e: Exception) {
                // ถ้าดึงไม่สำเร็จ ก็ปล่อยเป็น List ว่างๆ ไว้ก่อน
                _provinceList.value = emptyList()
            }
        }
    }

    // ฟังก์ชันสร้างทริป (เหมือนเดิม)
    fun createNewTrip(
        tripName: String,
        province: String,
        startDateStr: String,
        endDateStr: String,
        userId: String,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val request = CreateTripRequest(
                    trip_name  = tripName,
                    province   = province,
                    start_date = formatToMySQLDate(startDateStr),
                    end_date   = formatToMySQLDate(endDateStr) ?: formatToMySQLDate(startDateStr),
                    user_id    = userId
                )
                val response = TripPlanClient.apiService.createTrip(request)
                onSuccess(response.trip_id ?: "")
            } catch (e: Exception) {
                onError(e.message ?: "เกิดข้อผิดพลาดในการเชื่อมต่อ")
            }
        }
    }

    private fun formatToMySQLDate(dateStr: String?): String? {
        if (dateStr.isNullOrBlank()) return null
        return try {
            val parts = dateStr.split("/")
            when (parts.size) {
                2 -> {
                    val day   = parts[0].padStart(2, '0')
                    val month = parts[1].padStart(2, '0')
                    val year  = Calendar.getInstance().get(Calendar.YEAR)
                    "$year-$month-$day"
                }
                3 -> "${parts[2]}-${parts[1].padStart(2,'0')}-${parts[0].padStart(2,'0')}"
                else -> dateStr
            }
        } catch (e: Exception) { dateStr }
    }
}