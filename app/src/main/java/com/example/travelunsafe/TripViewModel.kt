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
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val request = CreateTripRequest(
                    trip_name  = tripName,
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