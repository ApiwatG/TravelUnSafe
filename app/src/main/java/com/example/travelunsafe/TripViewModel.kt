package com.example.travelunsafe

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.util.Calendar

class TripViewModel : ViewModel() {

    fun createNewTrip(
        tripName: String,
        startDateStr: String,
        endDateStr: String,
        userId: String,
        // 💡 แก้ให้ onSuccess ส่งคืนค่า String (ซึ่งก็คือ trip_id) กลับไป
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val startDate = formatToMySQLDate(startDateStr)
                val endDate = formatToMySQLDate(endDateStr) ?: startDate

                val request = CreateTripRequest(
                    trip_name = tripName,
                    start_date = startDate,
                    end_date = endDate,
                    user_id = userId
                )

                val response = TravelApiClient.apiService.createTrip(request)

                // 💡 ส่ง ID ทริปที่เพิ่งสร้างกลับไปให้ CreatePlanScreen
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
                    val day = parts[0].padStart(2, '0')
                    val month = parts[1].padStart(2, '0')
                    val year = Calendar.getInstance().get(Calendar.YEAR)
                    "$year-$month-$day"
                }
                3 -> {
                    val day = parts[0].padStart(2, '0')
                    val month = parts[1].padStart(2, '0')
                    val year = parts[2]
                    "$year-$month-$day"
                }
                else -> dateStr
            }
        } catch (e: Exception) {
            dateStr
        }
    }
}