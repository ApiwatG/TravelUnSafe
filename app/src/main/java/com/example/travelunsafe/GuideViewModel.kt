package com.example.travelunsafe

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.travelunsafe.model.ItineraryDto
import com.example.travelunsafe.network.ApiClient
import com.example.travelunsafe.ui.theme.DaySection
import com.example.travelunsafe.ui.theme.GuideAuthor
import com.example.travelunsafe.ui.theme.GuideDetailUiState
import com.example.travelunsafe.ui.theme.PlaceItem
import kotlinx.coroutines.launch

class GuideViewModel : ViewModel() {

    // สถานะเริ่มต้นจะว่างเปล่า เพื่อรอข้อมูลจาก Database
    var uiState by mutableStateOf(
        GuideDetailUiState(
            title = "กำลังโหลดข้อมูล...",
            author = GuideAuthor(name = "กำลังโหลด..."),
            days = emptyList()
        )
    )
        private set

    init {
        // ใช้ trip_id "T0001" ตามที่คุณระบุไว้
        fetchDataFromDatabase("T0001") 
    }

    fun fetchDataFromDatabase(tripId: String) {
        viewModelScope.launch {
            try {
                Log.d("GuideViewModel", "เริ่มดึงข้อมูลจาก Database สำหรับ ID: $tripId")
                val itineraries = ApiClient.api.getItinerary(tripId)
                
                if (itineraries.isNotEmpty()) {
                    uiState = mapDatabaseToUi(itineraries)
                    Log.d("GuideViewModel", "ดึงข้อมูลสำเร็จ: ${itineraries.size} รายการ")
                } else {
                    uiState = uiState.copy(title = "ไม่พบข้อมูลสำหรับ ID: $tripId")
                }
            } catch (e: Exception) {
                Log.e("GuideViewModel", "Error: ${e.message}")
                uiState = uiState.copy(title = "เกิดข้อผิดพลาด: ${e.localizedMessage}")
            }
        }
    }

    private fun mapDatabaseToUi(itineraries: List<ItineraryDto>): GuideDetailUiState {
        // จัดกลุ่มตามวันที่ (date) จากฐานข้อมูล MySQL
        val groupedByDate = itineraries.groupBy { it.date }
        
        val daySections = groupedByDate.entries.mapIndexed { index, entry ->
            DaySection(
                dayNumber = index + 1,
                places = entry.value.map { dbItem ->
                    PlaceItem(
                        name = dbItem.place_name,
                        tags = listOf("สถานที่"), // ข้อมูลเบื้องต้น
                        description = "พิกัด: ${dbItem.location}"
                    )
                }
            )
        }

        return GuideDetailUiState(
            title = "วิธีการเที่ยวญี่ปุ่นใน 2 สัปดาห์", // หัวข้อตามไกด์
            author = GuideAuthor(name = "White Snake"),
            days = daySections
        )
    }
}
