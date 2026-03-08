package com.example.travelunsafe

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

// ──────────────────────────────────────────────────────────
//  UI state passed to GuideDetailScreen
// ──────────────────────────────────────────────────────────
data class GuideAuthor(
    val name: String,
    val image: String? = null
)

data class PlaceItem(
    val name: String,
    val location: String? = null,
    val startTime: String? = null,
    val endTime: String? = null,
    val note: String? = null
)

data class DaySection(
    val dayNumber: Int,
    val date: String? = null,
    val places: List<PlaceItem>
)

data class GuideDetailUiState(
    val title: String,
    val author: GuideAuthor,
    val days: List<DaySection>,
    val isLoading: Boolean = false,
    val error: String? = null
)

// ──────────────────────────────────────────────────────────
//  ViewModel
// ──────────────────────────────────────────────────────────
class GuideViewModel : ViewModel() {

    var uiState by mutableStateOf(
        GuideDetailUiState(
            title    = "",
            author   = GuideAuthor(""),
            days     = emptyList(),
            isLoading = true
        )
    )
        private set

    // Load guide detail — guide metadata from TravelClient (port 3000),
    // itinerary data from TripPlanClient (port 3001)
    fun loadGuide(guide: GuideModel) {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, error = null)
            try {
                val itineraries = TripPlanClient.apiService
                    .getItineraryByTrip(guide.guide_id)

                uiState = GuideDetailUiState(
                    title    = guide.guide_name,
                    author   = GuideAuthor(
                        name  = guide.username ?: "ไม่ระบุชื่อ",
                        image = guide.image_profile
                    ),
                    days      = mapToDays(itineraries),
                    isLoading = false
                )
            } catch (e: Exception) {
                uiState = uiState.copy(
                    isLoading = false,
                    error     = "โหลดข้อมูลไม่สำเร็จ: ${e.message}"
                )
            }
        }
    }

    private fun mapToDays(itineraries: List<Itinerary>): List<DaySection> {
        // Group by date, assign day numbers in chronological order
        return itineraries
            .groupBy { it.date ?: "ไม่ระบุวัน" }
            .entries
            .sortedBy { it.key }
            .mapIndexed { index, (date, items) ->
                DaySection(
                    dayNumber = index + 1,
                    date      = date,
                    places    = items.map { it.toPlaceItem() }
                )
            }
    }

    private fun Itinerary.toPlaceItem() = PlaceItem(
        name      = place_name ?: "ไม่มีชื่อสถานที่",
        location  = location,
        startTime = start_time,
        endTime   = end_time,
        note      = note
    )
}
